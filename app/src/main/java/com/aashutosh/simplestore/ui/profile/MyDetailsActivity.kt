package com.aashutosh.simplestore.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.aashutosh.simplestore.R


class MyDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_details)
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        ivBack.setOnClickListener(View.OnClickListener {
            this.finish()
        })
    }
}