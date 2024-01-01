package com.example.tamashi

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.tamashi.PreferencesManager.Companion.ID_USER
import com.example.tamashi.PreferencesManager.Companion.JWT
import com.example.tamashi.PreferencesManager.Companion.SHARED_PREFS
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.InvocationTargetException

class DetailActivity : AppCompatActivity() {

    lateinit var sharedPrefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        sharedPrefs = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val backBtn: ImageView = findViewById(R.id.detail_back_btn)
        val productNameTop: TextView = findViewById(R.id.detail_name_tv_top)
        val productImage: ImageView = findViewById(R.id.detail_img)
        val productName: TextView = findViewById(R.id.detail_name_tv)
        val productPrice: TextView = findViewById(R.id.detail_price_tv)
        val qtyField: EditText = findViewById(R.id.detail_qty_et)
        val buyBtn: Button = findViewById(R.id.detail_buy_btn)

        val id_produk: String = intent.getStringExtra("id_produk").toString()

        backBtn.setOnClickListener{
            finish()
        }

        buyBtn.setOnClickListener{
            var jwt = sharedPrefs.getString(JWT, "")
            if(jwt == ""){
                if(Integer.parseInt(qtyField.text.toString()) > 0){
                    Toast.makeText(this, "Harap login terlebih dahulu", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Harap masukkan jumlah qty lebih dari 0", Toast.LENGTH_LONG).show()
                }
            } else {
                addOrder(qtyField, productPrice, id_produk)
            }
        }

        getProductData(id_produk, productNameTop, productImage, productName, productPrice)
    }

    private fun addOrder(qtyField: EditText, productPrice: TextView, id_produk: String) {
        val queue = Volley.newRequestQueue(this)
        val url = PreferencesManager.URL + "/api/orders"
        val orderobj = JSONObject()
        val dataObj = JSONObject()
        orderobj.put("qty", qtyField.text)
        orderobj.put("price", productPrice.text.toString().toDouble())
        orderobj.put("total_price", Integer.parseInt(qtyField.text.toString()) * productPrice.text.toString().toDouble())
        orderobj.put("users_permissions_user", sharedPrefs.getInt(ID_USER, 0))
        orderobj.put("products", id_produk)
        orderobj.put("status", "Waiting")
        dataObj.put("data", orderobj)

        val jsonRequest = object : JsonObjectRequest(Request.Method.POST, url, dataObj,
            Response.Listener { response ->
                val data = response.getJSONObject("data")
                if(data != null){
                    Toast.makeText(this, "Order telah dibuat!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, "Order gagal dibuat", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Log.e("ERROR ORDER", error.message.toString())
            }) {

            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer " + sharedPrefs.getString(JWT, "")
                return headers
            }
        }
        queue.add(jsonRequest)
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