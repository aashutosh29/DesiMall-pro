package com.aashutosh.simplestore.ui

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.aashutosh.simplestore.R
import com.aashutosh.simplestore.database.SharedPrefHelper
import com.aashutosh.simplestore.ui.onBoarding.OnBoarding
import com.aashutosh.simplestore.utils.Constant
import com.aashutosh.simplestore.viewModels.StoreViewModel
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
        sharedPrefHelper[Constant.GMAIL] = intent.getStringExtra(Constant.GMAIL)
        sharedPrefHelper[Constant.BRANCH_CODE] = intent.getStringExtra(Constant.BRANCH_CODE)
        Log.d(TAG, "gmail: ${intent.getStringExtra(Constant.GMAIL)}")
        mainViewModel =
            ViewModelProvider(this@SplashOldActivity)[StoreViewModel::class.java]

       /* if (intent.getStringExtra(Constant.BRANCH_CODE) == Constant.BRANCH_CODE) {
            startActivity(Intent(this@SplashOldActivity, HomeActivity::class.java))

        } else {*/
            GlobalScope.launch(Dispatchers.Main) {
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
                    if (sharedPrefHelper[Constant.FIRST_LOAD, true]) {
                        sharedPrefHelper[Constant.FIRST_LOAD] = false
                        startActivity(Intent(this@SplashOldActivity, OnBoarding::class.java))
                    } else {
                        startActivity(Intent(this@SplashOldActivity, HomeActivity::class.java))
                    }
                }
            }, 1500)
        }
    }
//}