package com.example.tamashi

import android.content.Context
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
import com.example.tamashi.PreferencesManager.Companion.JWT
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.InvocationTargetException

class SettingsActivity : AppCompatActivity() {

    lateinit var fullNameEt: EditText
    lateinit var curPassEt: EditText
    lateinit var newPass: EditText
    lateinit var saveName: Button
    lateinit var savePass: Button
    lateinit var backBtn: ImageView
    lateinit var sharedPrefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        fullNameEt = findViewById(R.id.settings_name_et)
        curPassEt = findViewById(R.id.settings_curpass_et)
        newPass = findViewById(R.id.settings_newpass_et)
        saveName = findViewById(R.id.settings_savename_btn)
        savePass = findViewById(R.id.settings_savepass_btn)
        backBtn = findViewById(R.id.settings_back_btn)
        sharedPrefs = getSharedPreferences(PreferencesManager.SHARED_PREFS, Context.MODE_PRIVATE)
        fullNameEt.setText(intent.getStringExtra("full_name"))

        backBtn.setOnClickListener{
            finish()
        }

        saveName.setOnClickListener{
            saveFullName()
        }

        savePass.setOnClickListener{
            savePassword()
        }
    }

    private fun savePassword(){
        val curPassText = curPassEt.text
        val newPassText = newPass.text
        val passObject = JSONObject()
        passObject.put("currentPassword", curPassText)
        passObject.put("password", newPassText)
        passObject.put("passwordConfirmation", newPassText)
        val url = PreferencesManager.URL + "/api/auth/change-password"
        val queue = Volley.newRequestQueue(this)

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, passObject,
            Response.Listener { response ->
                try {
                    Toast.makeText(this, "Success Change Password", Toast.LENGTH_LONG).show()
                    finish()
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
                val headers = HashMap<String, String>();
                headers["Authorization"] = "Bearer " + sharedPrefs.getString(JWT, "")
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }

    private fun saveFullName() {
        val fullnameText = fullNameEt.text.toString()
        val fullnameObject = JSONObject()
        fullnameObject.put("full_name", fullnameText)
        val url = PreferencesManager.URL + "/api/users/" + sharedPrefs.getInt(PreferencesManager.ID_USER, 0)
        val queue = Volley.newRequestQueue(this)

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.PUT, url, fullnameObject,
            Response.Listener { response ->
                try {
                    val newName = response.getString("full_name").toString()
                    fullNameEt.setText(newName)
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
                headers["Authorization"] = "Bearer " + sharedPrefs.getString(PreferencesManager.JWT, "")
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }
}