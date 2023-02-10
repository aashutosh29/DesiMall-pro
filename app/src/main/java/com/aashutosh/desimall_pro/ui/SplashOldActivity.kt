package com.aashutosh.desimall_pro.ui

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.databinding.ActivityFastSplashBinding
import com.aashutosh.desimall_pro.ui.detailsVerificationPage.DetailsVerificationActivity
import com.aashutosh.desimall_pro.ui.mapActivity.MapsActivity
import com.aashutosh.desimall_pro.ui.onBoarding.OnBoardingActivity
import com.aashutosh.desimall_pro.ui.phoneVerification.EnterNumberActivity
import com.aashutosh.desimall_pro.utils.Constant
import com.aashutosh.desimall_pro.viewModels.StoreViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashOldActivity : AppCompatActivity() {
    lateinit var mainViewModel: StoreViewModel
    private lateinit var sharedPrefHelper: SharedPrefHelper
    lateinit var binding: ActivityFastSplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFastSplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvMarquee.isSelected = true
    }


    @OptIn(DelicateCoroutinesApi::class)
    override fun onStart() {
        super.onStart()
        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(applicationContext)
        //sharedPrefHelper[Constant.BRANCH_NAME] = intent.getStringExtra(Constant.BRANCH_NAME)
        sharedPrefHelper[Constant.VERIFIED_LOCATION] = true
        mainViewModel =
            ViewModelProvider(this@SplashOldActivity)[StoreViewModel::class.java]

        if (sharedPrefHelper[Constant.BRANCH_CODE, ""] == intent.getStringExtra(Constant.BRANCH_CODE)
        ) {
            navigationalRoute()
        } else {
            if (intent.getStringExtra(Constant.BRANCH_CODE) != null) {
                sharedPrefHelper[Constant.BRANCH_CODE] =
                    intent.getStringExtra(Constant.BRANCH_CODE)

            }
            binding.tvLBF.typeWrite(this, "LOADING BEST OFFERS...", 65L)
            GlobalScope.launch {
                if (mainViewModel.getDesiProduct(
                        intent.getStringExtra(Constant.BRANCH_CODE)!!.toInt(),
                    )
                ) {
                    if (mainViewModel.getFilteredCategoryFromProduct()) {
                        navigationalRoute()
                    }
                }
            }
            // binding.clNewLocation.visibility = View.VISIBLE

        }

    }

    private fun navigationalRoute() {
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
        } else if (!sharedPrefHelper[Constant.DETAILIlS_VERIFIED, false]) {
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

    private fun TextView.typeWrite(
        lifecycleOwner: LifecycleOwner,
        text: String,
        intervalMs: Long,
    ) {
        this@typeWrite.text = ""
        lifecycleOwner.lifecycleScope.launch {
            val max = 15
            for (i in 0 until max) {
                repeat(text.length) {
                    delay(intervalMs)
                    Log.d(TAG, "typeWrite: $it")
                    this@typeWrite.text = text.take(it + 1)
                }
                delay(1200)

            }
        }
    }

}
