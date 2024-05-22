package com.aashutosh.desimall_pro.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.databinding.ActivityHomeBinding
import com.aashutosh.desimall_pro.databinding.ActivityPasswordBinding
import com.aashutosh.desimall_pro.splash.test.FindNearestStoreActivity
import com.aashutosh.desimall_pro.ui.detailsVerificationPage.DetailsVerificationActivity
import com.aashutosh.desimall_pro.utils.Constant
import com.facebook.login.Login
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class PasswordActivity : AppCompatActivity() {
    lateinit var sharedPrefHelper: SharedPrefHelper
    lateinit var binding: ActivityPasswordBinding
    var isForgetPass : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordBinding.inflate(layoutInflater)

        setContentView(binding.root)
        isForgetPass = intent.getBooleanExtra(Constant.IS_FORGET_PASSWORD, false)
        if (isForgetPass){
            binding.tbTitle.text = "Change Password"

        }
        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(this)
        binding.btSavePWD.setOnClickListener(View.OnClickListener {
            savePassword()
        })
    }

    private fun savePassword() {

        val password = binding.etPass.text.toString()
        val confirmPassword = binding.etConPass.text.toString()

        if (password.length < 6) {
            binding.etPass.error = "Password must be at least 6 characters"
            return
        }

        if (password != confirmPassword) {
            binding.etConPass.error = "Passwords do not match"
            return
        }

        // Password validation passed, and passwords match
        // Save the password to the database or any other storage mechanism
        // For demonstration purposes, let's just show a Toast indicating success

        showLoading(true)
        val firestore = FirebaseFirestore.getInstance()
        val userCollection = firestore.collection("user")

        userCollection.document(sharedPrefHelper[Constant.PHONE_NUMBER, ""]).get()
            .addOnSuccessListener { documentSnapshot: DocumentSnapshot? ->
                // Hide the loading indicator after Firestore operation completes
                showLoading(false)

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val newPassword = binding.etPass.text.toString()
                    userCollection.document(sharedPrefHelper[Constant.PHONE_NUMBER, ""])
                        .update("password", newPassword)
                        .addOnSuccessListener {
                            if (isForgetPass){
                                val i = Intent(this@PasswordActivity, LoginActivity::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(i)
                            }
                            else{

                                sharedPrefHelper[Constant.LOGIN_SUCCESS] = true

                                val i = Intent(this@PasswordActivity, RequestForFetchingLocationActivity::class.java)
                                i.putExtra(Constant.IS_FOR_DETAILS_VERIFICATION,true)
                                startActivity(i)
//                            val i = Intent(this@PasswordActivity, DetailsVerificationActivity::class.java)
//                            startActivity(i)
                            }
                            // Password updated successfully
                            Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { exception ->
                            // Handle the failure to update password
                            Toast.makeText(this, "Failed to update password: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    // ... (Update password or create new user logic remains unchanged)
                } else {
                    Toast.makeText(this, "Error, Try Again latter", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                // Hide the loading indicator if an error occurs during the Firestore operation
                showLoading(false)

                // Handle the failure to retrieve user data
                Toast.makeText(this, "Failed to retrieve user data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }


        // Optionally, you can navigate back to the previous activity or take further actions
        // For example, you can start the main activity or any other activity in your app
        // startActivity(Intent(this, MainActivity::class.java))
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) ProgressBar.VISIBLE else ProgressBar.GONE
    }

}