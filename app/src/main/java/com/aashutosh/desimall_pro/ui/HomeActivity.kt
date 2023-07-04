package com.aashutosh.desimall_pro.ui

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.databinding.ActivityHomeBinding
import com.aashutosh.desimall_pro.ui.cartActivity.CartActivity
import com.aashutosh.desimall_pro.ui.fragments.HomeFragment
import com.aashutosh.desimall_pro.ui.fragments.NotificationFragment
import com.aashutosh.desimall_pro.ui.fragments.ProfileFragment
import com.aashutosh.desimall_pro.utils.Constant
import com.aashutosh.desimall_pro.viewModels.StoreViewModel
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var mainViewModel: StoreViewModel
    lateinit var sharedPrefHelper: SharedPrefHelper
    lateinit var binding: ActivityHomeBinding

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(this)
        mainViewModel = ViewModelProvider(this@HomeActivity)[StoreViewModel::class.java]
        GlobalScope.launch(Dispatchers.Main) {
            if (mainViewModel.getDesiProduct(
                    sharedPrefHelper[Constant.BRANCH_CODE, ""].toInt()
                )
            ) {
                if (mainViewModel.getFilteredCategoryFromProduct()) {
                    Toast.makeText(this@HomeActivity, "Success", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "onCreateView: product updated")
                }
            }

            mainViewModel.getCartSize()

        }

        mainViewModel.cartSize.observe(this@HomeActivity, Observer {
            initCartIcon(it.toString())

        })


        if (intent.getBooleanExtra(Constant.IS_NOTIFICATION, false)) {
            binding.bottomNav.selectedItemId = R.id.notification
            loadFragment(NotificationFragment())
        } else if (intent.getBooleanExtra(Constant.IS_PROFILE, false)) {
            binding.bottomNav.selectedItemId = R.id.profile
            loadFragment(ProfileFragment())
        } else
            loadFragment(HomeFragment())
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.notification -> {
                    loadFragment(NotificationFragment())
                    true
                }
                R.id.cart -> {
                    val intent = Intent(this@HomeActivity, CartActivity::class.java)
                    startActivity(intent)
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

    private fun initCartIcon(count: String) {
        val mBottomNavigationMenuView = binding.bottomNav.getChildAt(0) as BottomNavigationMenuView
        val chatBadge = LayoutInflater.from(this).inflate(
            R.layout.item_count,
            mBottomNavigationMenuView, false
        )
        val itemView = mBottomNavigationMenuView.getChildAt(1) as BottomNavigationItemView
        val tvUnreadChats = chatBadge.findViewById(R.id.tvUnreadChats) as TextView
        tvUnreadChats.text = count//String that you want to show in badge
        itemView.addView(chatBadge)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        loadFragment(HomeFragment())
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}