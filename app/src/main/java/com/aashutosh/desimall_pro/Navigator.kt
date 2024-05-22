package com.aashutosh.desimall_pro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.databinding.ActivityNavigatorBinding
import com.aashutosh.desimall_pro.ui.HomeActivity
import com.aashutosh.desimall_pro.ui.LoginActivity
import com.aashutosh.desimall_pro.ui.RequestForFetchingLocationActivity
import com.aashutosh.desimall_pro.ui.detailsVerificationPage.DetailsVerificationActivity
import com.aashutosh.desimall_pro.utils.Constant
import com.facebook.login.Login
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@AndroidEntryPoint

class Navigator : AppCompatActivity() {
    lateinit var binding: ActivityNavigatorBinding
    private lateinit var sharedPrefHelper: SharedPrefHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavigatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(applicationContext)
        navigationalRoute()
    }
    private fun navigationalRoute() {

//        testing route
        val testing = false
        if (testing) {

        } else {


            if (sharedPrefHelper[Constant.LOGIN_SUCCESS, false] && sharedPrefHelper[Constant.DETAILIlS_VERIFIED, false]) {
                val i = Intent(this@Navigator, HomeActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
            } else if (sharedPrefHelper[Constant.LOGIN_SUCCESS, false]) {
                val i = Intent(this@Navigator, DetailsVerificationActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
            } else {
                val i = Intent(this@Navigator, LoginActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
            }
        }
    }
}