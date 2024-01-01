package com.example.tamashi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tamashi.adapters.ProductAdapter
import com.example.tamashi.models.ProductModel
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.InvocationTargetException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val recycler = view.findViewById<RecyclerView>(R.id.home_recycler)
        val loading = view.findViewById<ProgressBar>(R.id.home_pb)

        callProductApi(recycler, loading)

        return view
    }

    fun callProductApi(recycler: RecyclerView, loading: ProgressBar)
    {
        loading.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(requireContext())
        val url = PreferencesManager.URL + "/api/products?populate=*"

        val stringRequest = StringRequest(Request.Method.GET, url,
            {
                response ->
                val productList = callManuallyData(response)

                val productAdapter = ProductAdapter()
                recycler.adapter = productAdapter
                loading.visibility = View.GONE
                productAdapter.submitList(productList)
            },{
                error ->
                error.printStackTrace();
                loading.visibility = View.GONE
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_LONG).show()
            })
        queue.add(stringRequest)
    }

    private fun callManuallyData(response: String) : ArrayList<ProductModel> {
        val productList = arrayListOf<ProductModel>()
        try {
            val responseObject = JSONObject(response)
            val productArray = responseObject.getJSONArray("data")

            for (index in 0 until productArray.length()){
                val productObject = productArray.getJSONObject(index)
                val products = productObject.getJSONObject("attributes")
                val thumbnail = products.getJSONObject("thumbnail")
                val thumbnailData = thumbnail.getJSONObject("data")
                val thumbnailAttr = thumbnailData.getJSONObject("attributes")

                val product = ProductModel(
                    productObject.getInt("id"),
                    products.getString("name"),
                    products.getString("description"),
                    products.getDouble("price"),
                    thumbnailAttr.getString("url"),
                    products.getString("status"),
                    products.getString("po_date"),
                    products.getInt("price")
                )
                productList.add(product)
            }

        } catch (e: InvocationTargetException){
            Log.e("ERROR", "error ITE: " + e.message)
        }
        return productList
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}