package com.example.tamashi

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.tamashi.PreferencesManager.Companion.ID_USER
import com.example.tamashi.PreferencesManager.Companion.SHARED_PREFS
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    lateinit var bottomNav: BottomNavigationView;
    lateinit var searchBar: EditText;
    lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Load Default Fragment
        loadFragment(HomeFragment());

        sharedPrefs = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

        bottomNav = findViewById(R.id.main_botnav);
        searchBar = findViewById(R.id.main_search_et)
        bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_categories -> {
                    loadFragment(CategoriesFragment())
                    true
                }
                R.id.nav_account -> {
                    var id_user: Int? = sharedPrefs.getInt(ID_USER, 0)
                    if(id_user == 0){
                        startActivity(Intent(this, LoginActivity::class.java))
                        true
                    } else {
                        loadFragment(AccountFragment())
                        true
                    }
                }

                else -> {
                    false
                }
            }
        }

    }

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction();
        transaction.replace(R.id.main_frame, fragment);
        transaction.commit();
    }
}