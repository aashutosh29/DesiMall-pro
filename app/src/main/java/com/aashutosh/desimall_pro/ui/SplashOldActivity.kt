package com.aashutosh.desimall_pro.ui

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.ui.detailsVerificationPage.DetailsVerificationActivity
import com.aashutosh.desimall_pro.ui.mapActivity.MapsActivity
import com.aashutosh.desimall_pro.ui.onBoarding.OnBoardingActivity
import com.aashutosh.desimall_pro.ui.phoneVerification.EnterNumberActivity
import com.aashutosh.desimall_pro.utils.Constant
import com.aashutosh.desimall_pro.viewModels.StoreViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashOldActivity : AppCompatActivity() {
    lateinit var mainViewModel: StoreViewModel
    private lateinit var sharedPrefHelper: SharedPrefHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fast_splash)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onStart() {
        super.onStart()
        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper[Constant.BRANCH_NAME] = intent.getStringExtra(Constant.BRANCH_NAME)
        sharedPrefHelper[Constant.BRANCH_NAME] = intent.getStringExtra(Constant.BRANCH_NAME)

        sharedPrefHelper[Constant.VERIFIED_LOCATION] = true
        Log.d(TAG, "gmail: ${intent.getStringExtra(Constant.BRANCH_NAME)}")
        mainViewModel =
            ViewModelProvider(this@SplashOldActivity)[StoreViewModel::class.java]

            GlobalScope.launch(Dispatchers.Main) {
                sharedPrefHelper[Constant.BRANCH_CODE] =
                    intent.getStringExtra(Constant.BRANCH_CODE)
                /*if (sharedPrefHelper[Constant.BRANCH_CODE, ""] != intent.getStringExtra(Constant.BRANCH_CODE)) {
                    sharedPrefHelper[Constant.LOCATION_CHANGED] = true
                    sharedPrefHelper[Constant.BRANCH_CODE] =
                        intent.getStringExtra(Constant.BRANCH_CODE)
                }*/
                if (sharedPrefHelper[Constant.FIRST_LOAD, true]) {
                    mainViewModel.getDesiProduct(
                        intent.getStringExtra(Constant.BRANCH_CODE)!!.toInt(),
                        true
                    )
                    mainViewModel.getAllDesiProduct()
                }
            }
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    sharedPrefHelper.init(applicationContext)
                    if (!sharedPrefHelper[Constant.FIRST_LOAD, false]) {
                        sharedPrefHelper[Constant.FIRST_LOAD] = true
                        val i = Intent(this@SplashOldActivity, OnBoardingActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(i)
                    } else if (sharedPrefHelper[Constant.USER_SKIPPED, false]) {
                        val i = Intent(this@SplashOldActivity, HomeActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(i)
                    } else if (!sharedPrefHelper[Constant.VERIFIED_NUM, false]) {
                        val i = Intent(this@SplashOldActivity, EnterNumberActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(i)
                    } else if (!sharedPrefHelper[Constant.VERIFIED_LOCATION, false]) {
                        val i = Intent(this@SplashOldActivity, MapsActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        i.putExtra(Constant.VERIFY_USER_LOCATION, true)
                        startActivity(i)
                    } else if (!sharedPrefHelper[Constant.DETAIlS_VERIFED, false]) {
                        val i = Intent(this@SplashOldActivity, DetailsVerificationActivity::class.java)
                        i.putExtra(Constant.VERIFY_USER_LOCATION, true)
                        i.putExtra(Constant.DETAILS, true)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(i)
                    } else {
                        val i = Intent(this@SplashOldActivity, HomeActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(i)
                    }
                }
            }, 1500)
        }
    }
//}

/*else if (!sharedPrefHelper[Constant.VERIFIED_LOCATION, false]) {
                        val i = Intent(this@SplashOldActivity, MapsActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        i.putExtra(Constant.VERIFY_USER_LOCATION, true)
                        startActivity(i)
                    }*/