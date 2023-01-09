package com.aashutosh.desimall_pro.ui.orderHistoryActivity.orderHistoryDetails

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aashutosh.desimall_pro.databinding.ActivityOrderHistoryDetailsBinding

class OrderHistoryDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityOrderHistoryDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}