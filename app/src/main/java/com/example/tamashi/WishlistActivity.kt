package com.example.tamashi

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tamashi.PreferencesManager.Companion.ID_USER
import com.example.tamashi.PreferencesManager.Companion.JWT
import com.example.tamashi.PreferencesManager.Companion.SHARED_PREFS
import com.example.tamashi.adapters.WishlistAdapter
import com.example.tamashi.models.WishlistModel
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.InvocationTargetException

class WishlistActivity : AppCompatActivity() {

    lateinit var backBtn: ImageView
    lateinit var recycler: RecyclerView
    lateinit var sharedPrefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wishlist)

        sharedPrefs = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        backBtn = findViewById(R.id.wishlist_back_btn)
        recycler = findViewById(R.id.wishlist_recycler)

        callWishlistApi()

        backBtn.setOnClickListener{
            finish()
        }
    }

    private fun callWishlistApi() {
        val queue = Volley.newRequestQueue(this)
        val url = PreferencesManager.URL + "/api/users/"+sharedPrefs.getInt(ID_USER, 0)+"?populate[0]=wishlists&populate[1]=wishlists.products.thumbnail"

        val stringRequest = object : StringRequest(Request.Method.GET, url,
            {
                response ->
                val wishlistList = callData(response)
                val wishlistAdapter = WishlistAdapter()
                recycler.adapter = wishlistAdapter
                wishlistAdapter.submitList(wishlistList)
            },
            {
                error ->
//                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
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

    private fun callData(response: String?): MutableList<WishlistModel>? {
        val wishlistList = arrayListOf<WishlistModel>()
        try {
            val responseObj = JSONObject(response)
            val wishlistData = responseObj.getJSONArray("wishlists")

            for (index in 0 until wishlistData.length()){
                val wishlistObj = wishlistData.getJSONObject(index)
                val productsArr = wishlistObj.getJSONArray("products")
                val productObj = productsArr.getJSONObject(0)
                val thumbnail = productObj.getJSONObject("thumbnail")

                val wishlist = WishlistModel(
                    wishlistObj.getInt("id"),
                    productObj.getInt("id"),
                    productObj.getString("name"),
                    thumbnail.getString("url"),
                    productObj.getDouble("price")
                )
                wishlistList.add(wishlist)
            }
        } catch (e: InvocationTargetException){
            Log.e("ERROR ITE", e.message.toString())
        } catch (e: JSONException){
            Log.e("ERROR JSON", e.message.toString())
        }
        return wishlistList
    }
}