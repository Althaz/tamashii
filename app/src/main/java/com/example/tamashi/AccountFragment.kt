package com.example.tamashi

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tamashi.PreferencesManager.Companion.ID_USER
import com.example.tamashi.PreferencesManager.Companion.JWT
import com.example.tamashi.PreferencesManager.Companion.SHARED_PREFS
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.InvocationTargetException
import kotlin.math.log

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var sharedPrefs: SharedPreferences
    lateinit var addressText: String
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
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        val accName = view.findViewById<TextView>(R.id.account_name_tv)
        val accEmail = view.findViewById<TextView>(R.id.account_email_tv)
        sharedPrefs = view.context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
//        Buttons Menu
        val wishlistBtn = view.findViewById<LinearLayout>(R.id.account_wishlist_btn)
        val ordersBtn = view.findViewById<LinearLayout>(R.id.account_orders_btn)
        val addressBtn = view.findViewById<LinearLayout>(R.id.account_address_btn)
        val settingBtn = view.findViewById<LinearLayout>(R.id.account_setting_btn)
        val logoutBtn = view.findViewById<Button>(R.id.account_logout_btn)

        logoutBtn.setOnClickListener{
            val editSp = sharedPrefs.edit()
            editSp.remove(JWT)
            editSp.remove(ID_USER)
            editSp.apply()
            startActivity(Intent(requireContext(), LoginActivity::class.java));
        }

        wishlistBtn.setOnClickListener{
            startActivity(Intent(requireContext(), WishlistActivity::class.java))
        }

        ordersBtn.setOnClickListener{
            startActivity(Intent(requireContext(), OrdersActivity::class.java))
        }

        addressBtn.setOnClickListener{
            val intent = Intent(requireContext(), AddressActivity::class.java)
            intent.putExtra("address", addressText)
            requireContext().startActivity(intent)
        }

        getAccountData(accName, accEmail)

        return view
    }

    private fun getAccountData(accName: TextView?, accEmail: TextView?) {
        var jwt: String? = sharedPrefs.getString(JWT, "")
        val url = PreferencesManager.URL + "/api/users/" + sharedPrefs.getInt(ID_USER, 0) + "?populate[wishlists][populate][0]=products"
        Log.d("URL", url.toString())
        val queue = Volley.newRequestQueue(requireContext())

        val stringRequest: StringRequest = object : StringRequest(Request.Method.GET, url,
            Response.Listener { response ->
                Log.e("RES", response)
                try {
                    val res = JSONObject(response)
                    accName?.text = res.getString("full_name")
                    accEmail?.text = res.getString("email")
                    addressText = res.getString("address")
                } catch (e: InvocationTargetException){
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                    Log.e("ERROR ITE", e.message.toString())
                } catch (e: JSONException){
                    Log.e("ERROR JSON", e.message.toString())
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_LONG).show()
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer " + jwt.toString()
                return headers
            }
        }

        queue.add(stringRequest)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}