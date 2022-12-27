package com.aashutosh.desimall_pro.ui.onBoarding

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.adapter.OnboardingViewPagerAdapter
import com.aashutosh.desimall_pro.databinding.ActivityOnBoardingBinding
import com.aashutosh.desimall_pro.utils.Animatoo
import com.google.android.material.tabs.TabLayoutMediator

class OnBoarding : AppCompatActivity() {

    private lateinit var mViewPager: ViewPager2
    private lateinit var textSkip: TextView

    private lateinit var binding: ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mViewPager = binding.viewPager
        mViewPager.adapter = OnboardingViewPagerAdapter(this, this)
        TabLayoutMediator(binding.pageIndicator, mViewPager) { _, _ -> }.attach()
        textSkip = findViewById(R.id.text_skip)
        textSkip.setOnClickListener {
            finish()
            val intent =
                Intent(applicationContext, OnboardFinishActivity::class.java)
            startActivity(intent)
            Animatoo.animateSlideLeft(this)
        }

        val btnNextStep: Button = findViewById(R.id.btn_next_step)

        btnNextStep.setOnClickListener {
            if (getItem() > mViewPager.childCount-1) {
                finish()
                val intent =
                    Intent(applicationContext, OnboardFinishActivity::class.java)
                startActivity(intent)
                Animatoo.animateSlideLeft(this)
            } else {
                mViewPager.setCurrentItem(getItem() + 1, true)
            }
        }

    }

    private fun getItem(): Int {
        return mViewPager.currentItem
    }

}
