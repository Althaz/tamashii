package com.example.tamashi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    lateinit var bottomNav: BottomNavigationView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Load Default Fragment
        loadFragment(HomeFragment());

        bottomNav = findViewById(R.id.main_botnav);
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
                    loadFragment(AccountFragment())
                    true
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