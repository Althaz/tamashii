package com.example.tamashi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        val identifier = findViewById<EditText>(R.id.login_username_et)
        val password = findViewById<EditText>(R.id.login_pass_et)
        val toRegister = findViewById<TextView>(R.id.login_to_reg_btn)
        val loginBtn = findViewById<Button>(R.id.login_login_btn)

        loginBtn.setOnClickListener{
            Toast.makeText(this@LoginActivity, "Login", Toast.LENGTH_LONG)
            getAuthData(identifier, password)
        }
    }

    private fun getAuthData(identifier: EditText?, password: EditText?) {
        val strIdentifier = identifier?.text.toString()
        val strPass = password?.text.toString()

        val authObject = JSONObject()
        authObject.put("identifier", strIdentifier)
        authObject.put("password", strPass)
        val url = PreferencesManager.URL + "/api/auth/local"

        val jsonRequest = object : JsonObjectRequest(
            Request.Method.POST, url, authObject,
            Response.Listener { response ->
                try {
                    val jwtRes = response.getString("jwt")
                    val userRes = response.getJSONObject("user")
                    PreferencesManager.JWT = response.getString("jwt")
                    PreferencesManager.ID_USER = userRes.getInt("id")

                    Log.d("Hasil", response.toString())
                    startActivity(Intent(this, MainActivity::class.java))
                } catch (e: JSONException){
                    Toast.makeText(this, e.message, Toast.LENGTH_LONG)
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG)
            }
            ) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

//            override fun getHeaders(): MutableMap<String, String> {
//                return super.getHeaders()
//            }
        }
        val queue = Volley.newRequestQueue(this);
        queue.add(jsonRequest)
    }
}