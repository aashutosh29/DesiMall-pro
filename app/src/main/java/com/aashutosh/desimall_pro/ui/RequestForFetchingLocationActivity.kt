package com.aashutosh.desimall_pro.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.aashutosh.desimall_pro.databinding.ActivityStoreFindingBinding
import com.aashutosh.desimall_pro.splash.test.FindNearestStoreActivity

class RequestForFetchingLocationActivity : AppCompatActivity() {
    lateinit var binding: ActivityStoreFindingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreFindingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.clSignIn.setOnClickListener(View.OnClickListener {
            val i = Intent(this@RequestForFetchingLocationActivity, FindNearestStoreActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        })
    }
}