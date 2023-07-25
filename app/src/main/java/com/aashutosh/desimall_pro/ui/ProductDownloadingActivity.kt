package com.aashutosh.desimall_pro.ui

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.databinding.ActivityFastSplashBinding
import com.aashutosh.desimall_pro.splash.test.FindNearestStoreActivity
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
class ProductDownloadingActivity : AppCompatActivity() {
    lateinit var mainViewModel: StoreViewModel
    private lateinit var sharedPrefHelper: SharedPrefHelper
    lateinit var binding: ActivityFastSplashBinding
    var isForDetailsVerification : Boolean = false
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
        isForDetailsVerification = intent.getBooleanExtra(Constant.IS_FOR_DETAILS_VERIFICATION, false)
        mainViewModel =
            ViewModelProvider(this@ProductDownloadingActivity)[StoreViewModel::class.java]


        if (isForDetailsVerification ){
            val branchCode = intent.getStringExtra(Constant.BRANCH_CODE)!!.toInt()
            GlobalScope.launch {
                if (mainViewModel.getDesiProduct(
                        branchCode,
                    )
                ) {
                    if (mainViewModel.getFilteredCategoryFromProduct()) {
                        sharedPrefHelper[Constant.FETCHING_SUCCEED] = true

                    }
                }
            }
            val i = Intent(this@ProductDownloadingActivity, DetailsVerificationActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        }else{

        if (sharedPrefHelper[Constant.FETCHING_SUCCEED,false]){
            if (intent.getStringExtra(Constant.BRANCH_NAME)== Constant.BRANCH_NAME){
                binding.clMain.visibility = View.GONE
                binding.clLocationNotFound.visibility = View.VISIBLE
                binding.cvYes.setOnClickListener(View.OnClickListener {
                    binding.clMain.visibility = View.VISIBLE
                    binding.clLocationNotFound.visibility = View.GONE
                    if (intent.getStringExtra(Constant.BRANCH_CODE) != null) {
                        sharedPrefHelper[Constant.BRANCH_CODE] =
                            intent.getStringExtra(Constant.BRANCH_CODE)

                    }
                    val i = Intent(this@ProductDownloadingActivity, HomeActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)
                })
            }
            else{
            val i = Intent(this@ProductDownloadingActivity, HomeActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        }
        }
        if (intent.getStringExtra(Constant.BRANCH_NAME)== Constant.BRANCH_NAME){
            binding.clMain.visibility = View.GONE
            binding.clLocationNotFound.visibility = View.VISIBLE

            binding.cvYes.setOnClickListener(View.OnClickListener {
                binding.clMain.visibility = View.VISIBLE
                binding.clLocationNotFound.visibility = View.GONE
                if (intent.getStringExtra(Constant.BRANCH_CODE) != null) {
                    sharedPrefHelper[Constant.BRANCH_CODE] =
                        intent.getStringExtra(Constant.BRANCH_CODE)

                }
                binding.tvLBF.typeWrite(this, "LOADING BEST OFFERS...", 65L)
                try {
                    val branchCode = intent.getStringExtra(Constant.BRANCH_CODE)!!.toInt()
                    GlobalScope.launch {
                        if (mainViewModel.getDesiProduct(
                                branchCode,
                            )
                        ) {
                            if (mainViewModel.getFilteredCategoryFromProduct()) {
                                sharedPrefHelper[Constant.FETCHING_SUCCEED] = true
                                val i = Intent(this@ProductDownloadingActivity, HomeActivity::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(i)
                            }
                        }
                    }
                }catch (e: Exception){
                    val intent = Intent(this, FindNearestStoreActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    startActivity(intent)

                }


            })
        }
        else{
            binding.clMain.visibility = View.VISIBLE
            binding.clLocation.visibility = View.GONE


            if (sharedPrefHelper[Constant.BRANCH_CODE, ""] == intent.getStringExtra(Constant.BRANCH_CODE)
        ) {
          //  navigationalRoute()
                val i = Intent(this@ProductDownloadingActivity, HomeActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
        } else {
            if (intent.getStringExtra(Constant.BRANCH_CODE) != null) {
                sharedPrefHelper[Constant.BRANCH_CODE] =
                    intent.getStringExtra(Constant.BRANCH_CODE)

            }
            binding.tvLBF.typeWrite(this, "LOADING BEST OFFERS...", 65L)
            try {
                val branchCode = intent.getStringExtra(Constant.BRANCH_CODE)!!.toInt()
                GlobalScope.launch {
                    if (mainViewModel.getDesiProduct(
                           branchCode,
                        )
                    ) {
                        if (mainViewModel.getFilteredCategoryFromProduct()) {
                           // navigationalRoute()

                            val i = Intent(this@ProductDownloadingActivity, HomeActivity::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(i)
                        }
                    }
                }
            }catch (e: Exception){
                val intent = Intent(this, FindNearestStoreActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                startActivity(intent)

            }
        }
        }

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
