package com.aashutosh.desimall_pro

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aashutosh.desimall_pro.databinding.ActivityOrderSuccessBinding
import com.aashutosh.desimall_pro.ui.HomeActivity
import com.aashutosh.desimall_pro.ui.orderHistoryActivity.OrderHistoryActivity

class OrderSuccessActivity : AppCompatActivity() {
    lateinit var binding: ActivityOrderSuccessBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btBackToHome.setOnClickListener(View.OnClickListener {
            val i = Intent(this@OrderSuccessActivity, HomeActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        })
        binding.btOrderStatus.setOnClickListener(View.OnClickListener {
            val i = Intent(this@OrderSuccessActivity, OrderHistoryActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val i = Intent(this@OrderSuccessActivity, HomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

        startActivity(i)

    }


}