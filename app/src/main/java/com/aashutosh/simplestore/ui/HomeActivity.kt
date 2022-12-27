package com.aashutosh.simplestore.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.aashutosh.simplestore.R
import com.aashutosh.simplestore.ui.fragments.CategoryFragment
import com.aashutosh.simplestore.ui.fragments.HomeFragment
import com.aashutosh.simplestore.ui.fragments.NotificationFragment
import com.aashutosh.simplestore.ui.fragments.ProfileFragment
import com.aashutosh.simplestore.utils.Constant
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        if (intent.getBooleanExtra(Constant.IS_VIEW_ALL, false)) {
            bottomNav.selectedItemId = R.id.category
            loadFragment(CategoryFragment())
        } else if (intent.getBooleanExtra(Constant.IS_NOTIFICATION, false)) {
            bottomNav.selectedItemId = R.id.notification
            loadFragment(NotificationFragment())
        } else if (intent.getBooleanExtra(Constant.IS_PROFILE, false)) {
            bottomNav.selectedItemId = R.id.profile
            loadFragment(ProfileFragment())
        } else
            loadFragment(HomeFragment())
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.category -> {
                    loadFragment(CategoryFragment())
                    true
                }
                R.id.notification -> {
                    loadFragment(NotificationFragment())
                    true
                }
                R.id.profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> {
                    false
                }
            }
        }
    }


    fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}