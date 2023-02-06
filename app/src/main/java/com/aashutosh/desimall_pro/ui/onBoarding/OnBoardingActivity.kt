package com.aashutosh.desimall_pro.ui.onBoarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.databinding.ActivityOnBoardingBinding
import com.aashutosh.desimall_pro.ui.SplashOldActivity
import com.aashutosh.desimall_pro.utils.Constant

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var mViewPager: ViewPager2
    //private lateinit var textSkip: TextView

    private lateinit var binding: ActivityOnBoardingBinding
    private lateinit var sharedPrefHelper: SharedPrefHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(this)
        binding.clOTV.visibility = View.VISIBLE

        /* mViewPager = binding.viewPager
         mViewPager.adapter = OnboardingViewPagerAdapter(this, this)
         TabLayoutMediator(binding.pageIndicator, mViewPager) { _, _ -> }.attach()
         textSkip = findViewById(R.id.text_skip)*/
        binding.btNext.setOnClickListener {
            finish()
            val intent =
                Intent(applicationContext, SplashOldActivity::class.java)
            intent.putExtra(Constant.BRANCH_CODE, sharedPrefHelper[Constant.BRANCH_CODE, ""])
            startActivity(intent)
            // Animatoo.animateSlideLeft(this)
        }

//        val btnNextStep: Button = findViewById(R.id.btn_next_step)

        /* btnNextStep.setOnClickListener {
             if (getItem() > mViewPager.childCount-1) {
                 finish()
                 val intent =
                     Intent(applicationContext, SplashOldActivity::class.java)
                 startActivity(intent)
                 Animatoo.animateSlideLeft(this)
             } else {
                 mViewPager.setCurrentItem(getItem() + 1, true)
             }
         }*/

    }

    private fun getItem(): Int {
        return mViewPager.currentItem
    }

}
