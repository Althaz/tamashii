package com.example.tamashi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tamashi.adapters.CategoryAdapter
import com.example.tamashi.models.CategoryModel
import org.json.JSONObject
import java.lang.reflect.InvocationTargetException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CategoriesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoriesFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_categories, container, false)
        val recycler = view.findViewById<RecyclerView>(R.id.categories_recycler)

        callCategoriesApi(recycler)

        return view
    }

    private fun callCategoriesApi(recycler: RecyclerView?) {
        val queue = Volley.newRequestQueue(requireContext())
        val url = PreferencesManager.URL + "/api/categories"

        val stringRequest = StringRequest(Request.Method.GET, url,
            {
                response ->
                val categoryList = callData(response)

                val categoryAdapter = CategoryAdapter()
                recycler?.adapter = categoryAdapter
                categoryAdapter.submitList(categoryList)
            },{
                error ->
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_LONG)
            })
        queue.add(stringRequest)
    }

    private fun callData(response: String?): ArrayList<CategoryModel>? {
        val categoryList = arrayListOf<CategoryModel>()
        try {
            val responseObj = JSONObject(response)
            val categoriesArray = responseObj.getJSONArray("data")

            for (index in 0 until categoriesArray.length()){
                val categoryObj = categoriesArray.getJSONObject(index)
                val category = categoryObj.getJSONObject("attributes")

                val categoryModel = CategoryModel(
                    categoryObj.getInt("id"),
                    category.getString("name")
                )
                categoryList.add(categoryModel)
            }
        } catch (e: InvocationTargetException){
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG)
        }
        return categoryList
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CategoriesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CategoriesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}