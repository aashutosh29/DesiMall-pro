package com.aashutosh.desimall_pro.ui

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.ui.fragments.CategoryFragment
import com.aashutosh.desimall_pro.ui.fragments.HomeFragment
import com.aashutosh.desimall_pro.ui.fragments.NotificationFragment
import com.aashutosh.desimall_pro.ui.fragments.ProfileFragment
import com.aashutosh.desimall_pro.utils.Constant
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val user = FirebaseAuth.getInstance()
        Log.d(TAG, "GetTokenResult result = ")



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