package com.aashutosh.simplestore.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.aashutosh.simplestore.R
import com.aashutosh.simplestore.ui.HomeActivity
import com.aashutosh.simplestore.ui.onBoarding.OnboardFinishActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest

class SignIn : OnboardFinishActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        viewBind()
    }

    private fun viewBind() {
        val btSkip = findViewById<Button>(R.id.btSkip)
        val tvForgetPassword = findViewById<TextView>(R.id.tvForgetPassword)
        val signUP = findViewById<TextView>(R.id.tvRegister)
        val clSignIn = findViewById<ConstraintLayout>(R.id.clSignIn)
        val clGoogleSignIn = findViewById<ConstraintLayout>(R.id.clGoogleSignIn)
        clGoogleSignIn.setOnClickListener(View.OnClickListener {
            BeginSignInRequest.builder()
            signInGoogle()
        })
        btSkip.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@SignIn, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

        })
        clSignIn.setOnClickListener(View.OnClickListener {


        })

        signUP.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@SignIn, SignUp::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

        })


    }
}