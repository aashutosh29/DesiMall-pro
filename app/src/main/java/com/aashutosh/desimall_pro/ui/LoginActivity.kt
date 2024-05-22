package com.aashutosh.desimall_pro.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.databinding.ActivityLoginNewBinding
import com.aashutosh.desimall_pro.ui.phoneVerification.EnterNumberActivity
import com.aashutosh.desimall_pro.utils.Constant
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    lateinit var sharedPrefHelper: SharedPrefHelper
    lateinit var binding: ActivityLoginNewBinding
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userCollection = firestore.collection("user")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginNewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(this)


        binding.tvRegister.setOnClickListener(View.OnClickListener {
            val i = Intent(this@LoginActivity, EnterNumberActivity::class.java)
            i.putExtra(Constant.IS_FORGET_PASSWORD, false)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        })
        binding.ivBack.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })

      binding.btSignIn.setOnClickListener(View.OnClickListener {
          validateData()
      })

        binding.tvForgetPassword.setOnClickListener(View.OnClickListener {
            val i = Intent(this@LoginActivity, EnterNumberActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            i.putExtra(Constant.IS_FORGET_PASSWORD, true)
            startActivity(i)
        })

        binding.btSkip.setOnClickListener(View.OnClickListener {
            val i = Intent(this@LoginActivity, RequestForFetchingLocationActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        })
    }

    private fun validateData() {
        showLoading(true)
        val phoneNumber = binding.etPhoneNumber.text.toString()
        val password = binding.etPass.text.toString()

        if (phoneNumber.length != 10) {
            binding.etPhoneNumber.error = "Phone number must be 10 digits"
            showLoading(false)
            return
        }

        if (password.length <= 3) {
            binding.etPass.error = "Password must be at least 6 characters"
            showLoading(false)
            return
        }

        // Data is valid, proceed with further actions like signing in
        // For example, you can call a function here to handle the sign-in logic
         handleSignIn(phoneNumber, password)

        // For demonstration purposes, let's show a Toast indicating successful sign-in
//        val message = "Sign-in successful!\nPhone number: $phoneNumber\nPassword: $password"
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) ProgressBar.VISIBLE else ProgressBar.GONE
    }

    private fun handleSignIn(phoneNumber: String, password: String) {
        // Query Firestore based on the provided phoneNumber (documentID)
        userCollection.document(Constant.COUNTRY_CODE+phoneNumber)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Document with the provided phoneNumber (documentID) exists in Firestore
                    val storedPassword = documentSnapshot.getString("password")

                    if (storedPassword == password) {
                        // Password matches
                        // Proceed with login logic or other actions here

                        // For demonstration purposes, we'll show a success toast
                        showLoading(false)
                        sharedPrefHelper[Constant.LOGIN_SUCCESS] = true

                        // Store additional data in shared preferences
                        val userData = documentSnapshot.data
                        if (userData != null) {
                            sharedPrefHelper[Constant.EMAIL] = userData["email"].toString()
                            sharedPrefHelper[Constant.ZIP] = userData["zip"].toString()
                            sharedPrefHelper[Constant.NAME] = userData["name"].toString()
                            sharedPrefHelper[Constant.ADDRESS] = userData["location"].toString()
                            sharedPrefHelper[Constant.LAND_MARK] = userData["landmark"].toString()
                            sharedPrefHelper[Constant.PHOTO] = userData["photo"].toString()
                            sharedPrefHelper[Constant.PHONE_NUMBER] =userData["phone"].toString()
                            sharedPrefHelper[Constant.ADDRESS_FULL_DETAILS] = userData["fullAddress"].toString()

                            //Important details
                            sharedPrefHelper[Constant.BRANCH_NAME] = userData["branchName"]?.toString()?.trim() ?: "live desi mall"
                            sharedPrefHelper[Constant.BRANCH_CODE] = userData["branchCode"]?.toString()?.trim() ?: "2"

                        }

                        val message = "Sign-in successful!\nPhone number: $phoneNumber"
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                        /*if user login , then login success and detail verification must be true*/
                        sharedPrefHelper[Constant.LOGIN_SUCCESS] = true
                        sharedPrefHelper[Constant.DETAILIlS_VERIFIED] = true

                        // Start the next activity after successful login
                        val i = Intent(this@LoginActivity, HomeActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(i)
                    } else {
                        // Password doesn't match
                        showLoading(false)
                        Toast.makeText(this, "Username or password is incorrect", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Document with the provided phoneNumber (documentID) doesn't exist
                    showLoading(false)
                    Toast.makeText(this, "User with the provided ID not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                showLoading(false)
                // Handle network error and show a toast with an appropriate message
                Toast.makeText(this, "Network error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

}