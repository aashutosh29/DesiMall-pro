package com.aashutosh.desimall_pro.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.ui.HomeActivity
import com.aashutosh.desimall_pro.ui.onBoarding.OnboardFinishActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest

class SignUp : OnboardFinishActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        viewBind()
    }

    private fun viewBind() {
        val btSkip = findViewById<Button>(R.id.btSkip)
        val tvRegister: TextView = findViewById(R.id.tvRegister)
        val clSignUp = findViewById<ConstraintLayout>(R.id.clSignUp)
        val clGoogleSignIn = findViewById<ConstraintLayout>(R.id.clGoogleSignIn)
        clGoogleSignIn.setOnClickListener(View.OnClickListener {
            BeginSignInRequest.builder()
            signInGoogle()
        })
        btSkip.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@SignUp, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

        })
        tvRegister.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@SignUp, SignIn::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

        })

    }
}