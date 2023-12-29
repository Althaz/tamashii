package com.example.tamashi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.util.Objects

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val fullName = findViewById<EditText>(R.id.reg_full_name_et)
        val username = findViewById<EditText>(R.id.reg_username_et)
        val email = findViewById<EditText>(R.id.reg_email_et)
        val password = findViewById<EditText>(R.id.reg_pass_et)
        val regBtn = findViewById<Button>(R.id.reg_reg_btn)
        val toLogin = findViewById<TextView>(R.id.reg_to_login_btn)

        toLogin.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        regBtn.setOnClickListener{
            registerAccount(fullName, username, email, password)
        }
    }

    private fun registerAccount(fullName: EditText?, username: EditText?, email: EditText?, password: EditText?) {
        val strFullname: String = fullName?.text.toString()
        val strUsername: String = username?.text.toString()
        val strEmail: String = email?.text.toString()
        val strPassword: String = password?.text.toString()

        val regObject = JSONObject()
        regObject.put("username", strUsername)
        regObject.put("email", strEmail)
        regObject.put("password", strPassword)
        regObject.put("full_name", strFullname)

        val url = PreferencesManager.URL + "/api/auth/local/register"

        val jsonRequest = object : JsonObjectRequest(
            Request.Method.POST, url, regObject,
            Response.Listener { response ->
                try {
                    val userRes = response.getJSONObject("user")
                    PreferencesManager.JWT = response.getString("jwt")
                    PreferencesManager.ID_USER = userRes.getInt("id")

                    Log.d("Hasil", response.toString())
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } catch (e: JSONException){
                    Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG)
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG)
            }
        ){
            override fun getBodyContentType(): String {
                return "application/json"
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(jsonRequest)
    }
}