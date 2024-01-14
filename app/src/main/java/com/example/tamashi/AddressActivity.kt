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
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.tamashi.PreferencesManager.Companion.ID_USER
import com.example.tamashi.PreferencesManager.Companion.JWT
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.InvocationTargetException

class AddressActivity : AppCompatActivity() {

    lateinit var backBtn: ImageView
    lateinit var addressEt: EditText
    lateinit var saveBtn: Button
    lateinit var sharedPrefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        backBtn = findViewById(R.id.address_back_btn)
        addressEt = findViewById(R.id.address_address_et)
        saveBtn = findViewById(R.id.address_save_btn)
        sharedPrefs = getSharedPreferences(PreferencesManager.SHARED_PREFS, Context.MODE_PRIVATE)
        addressEt.setText(intent.getStringExtra("address"))

        backBtn.setOnClickListener{
            finish()
        }

        saveBtn.setOnClickListener{
            saveAddress()
        }
    }

    private fun saveAddress(){
        val addressText = addressEt.text.toString()
        val addressObject = JSONObject()
        addressObject.put("address", addressText)
        val url = PreferencesManager.URL + "/api/users/" + sharedPrefs.getInt(ID_USER, 0)
        val queue = Volley.newRequestQueue(this)

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.PUT, url, addressObject,
            Response.Listener { response ->
                try {
                    val newAddress = response.getString("address").toString()
                    addressEt.setText(newAddress)
                } catch (e: InvocationTargetException){
                    Log.e("ERROR ITE", e.message.toString())
                } catch (e: JSONException){
                    Log.e("ERROR JSON", e.message.toString())
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            }
        ){
            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer " + sharedPrefs.getString(JWT, "")
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }
}