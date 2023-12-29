package com.example.tamashi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.InvocationTargetException

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val backBtn: ImageView = findViewById(R.id.detail_back_btn)
        val productNameTop: TextView = findViewById(R.id.detail_name_tv_top)
        val productImage: ImageView = findViewById(R.id.detail_img)
        val productName: TextView = findViewById(R.id.detail_name_tv)
        val productPrice: TextView = findViewById(R.id.detail_price_tv)
        val buyBtn: Button = findViewById(R.id.detail_buy_btn)
        val addFavBtn: ImageView = findViewById(R.id.detail_add_wishlist_btn)

        val id_produk: String = intent.getStringExtra("id_produk").toString()

        backBtn.setOnClickListener{
            finish()
        }

        getProductData(id_produk, productNameTop, productImage, productName, productPrice)
    }

    private fun getProductData(
        idProduk: String,
        productNameTop: TextView,
        productImage: ImageView,
        productName: TextView,
        productPrice: TextView
    ) {
        val queue = Volley.newRequestQueue(this)
        val url = PreferencesManager.URL + "/api/products/" + idProduk + "?populate=*"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            {
                respose ->
                try {
                    val responseObject = JSONObject(respose)
                    val productData = responseObject.getJSONObject("data")
                    val productAttr = productData.getJSONObject("attributes")
                    val thumbnail = productAttr.getJSONObject("thumbnail")
                    val thumbnailData = thumbnail.getJSONObject("data")
                    val thumbnailAttr = thumbnailData.getJSONObject("attributes")

                    productNameTop.text = productAttr.getString("name")
                    Glide.with(productImage)
                        .load(PreferencesManager.URL + thumbnailAttr.getString("url"))
                        .into(productImage)
                    productName.text = productAttr.getString("name")
                    productPrice.text = productAttr.getDouble("price").toString()
                    Toast.makeText(this, productAttr.getString("name"), Toast.LENGTH_LONG)
                } catch (e: InvocationTargetException){
                    Log.e("ERRORITE", e.message.toString())
                } catch (e: JSONException){
                    Log.e("ERRORJSON", e.message.toString())
                }
            },{
                error ->
                error.printStackTrace()
                Toast.makeText(this, error.message, Toast.LENGTH_LONG)
            })
        queue.add(stringRequest)
    }
}