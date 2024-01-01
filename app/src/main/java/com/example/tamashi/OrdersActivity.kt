package com.example.tamashi

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tamashi.PreferencesManager.Companion.ID_USER
import com.example.tamashi.PreferencesManager.Companion.JWT
import com.example.tamashi.PreferencesManager.Companion.SHARED_PREFS
import com.example.tamashi.adapters.OrderAdapter
import com.example.tamashi.models.OrderModel
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.InvocationTargetException

class OrdersActivity : AppCompatActivity() {
    lateinit var backBtn: ImageView
    lateinit var recycler: RecyclerView
    lateinit var sharedPrefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        sharedPrefs = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        backBtn = findViewById(R.id.orders_back_btn)
        recycler = findViewById(R.id.orders_recycler)

        backBtn.setOnClickListener{
            finish()
        }

        callOrderApi()
    }

    private fun callOrderApi() {
        val queue = Volley.newRequestQueue(this)
        val url = PreferencesManager.URL + "/api/users/" + sharedPrefs.getInt(ID_USER, 0) +"?populate[0]=orders&populate[1]=orders.products.thumbnail"

        val stringRequest = object : StringRequest(Request.Method.GET, url,
            {
                response ->
                val orderList = callData(response)
                val orderAdapter = OrderAdapter();
                recycler.adapter = orderAdapter
                orderAdapter.submitList(orderList)
            },
            {
                error ->
                Log.e("RES ERROR", error.toString())
            }){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer " + sharedPrefs.getString(JWT, "")
                return headers
            }
        }
        queue.add(stringRequest)
    }

    private fun callData(response: String?): MutableList<OrderModel>? {
        val orderList = arrayListOf<OrderModel>()
        try {
            val responseObj = JSONObject(response)
            val orderArray = responseObj.getJSONArray("orders")

            for(index in 0 until orderArray.length()){
                val orderObj = orderArray.getJSONObject(index)
                val productsArr = orderObj.getJSONArray("products")
                val productObj = productsArr.getJSONObject(0)
                val thumbnailObj = productObj.getJSONObject("thumbnail")

                val orders = OrderModel(
                    orderObj.getInt("id"),
                    orderObj.getInt("qty"),
                    orderObj.getDouble("price"),
                    orderObj.getDouble("total_price"),
                    orderObj.getString("status"),
                    productObj.getString("name"),
                    thumbnailObj.getString("url")
                )
                orderList.add(orders)
            }
        } catch (e: InvocationTargetException){
            Log.e("ERROR ITE", e.message.toString())
        } catch (e: JSONException){
            Log.e("ERROR JSON", e.message.toString())
        }
        return orderList
    }
}