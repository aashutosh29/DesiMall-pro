package com.aashutosh.desimall_pro.ui.phoneVerification

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.databinding.ActivityEnterNumberBinding
import com.aashutosh.desimall_pro.ui.HomeActivity
import com.aashutosh.desimall_pro.ui.detailsVerificationPage.DetailsVerificationActivity
import com.aashutosh.desimall_pro.utils.Constant
import com.aashutosh.desimall_pro.utils.Constant.Companion.phoneNumberKey
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class EnterNumberActivity : AppCompatActivity() {
    var isForgetPass : Boolean = false
    private lateinit var binding: ActivityEnterNumberBinding
    lateinit var sharedPrefHelper: SharedPrefHelper
    private lateinit var progressDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnterNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isForgetPass = intent.getBooleanExtra(Constant.IS_FORGET_PASSWORD, false)
        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(this)
        if (isForgetPass){
            binding.imgPhoneIcon.text = "Change your account password"
            binding.tbTitle.text = "Change Password"
        }

        binding.btnGetOtp.setOnClickListener {
            validateNumber()
        }
        binding.ivBack.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })



    }
    private fun initProgressDialog(): AlertDialog {
        progressDialog = Constant.setProgressDialog(this,"Validating number")
        progressDialog.setCancelable(false) // blocks UI interaction
        return progressDialog
    }
    private fun checkPhoneNumberExistsForForgetPass(phoneNumber: String) {
        // Show the loading indicator before starting the Firestore operation
        initProgressDialog().show()
        // Check if the user exists with the provided phone number
        val firestore = FirebaseFirestore.getInstance()
        val userCollection = firestore.collection("user")
        val docReference = userCollection.document(Constant.COUNTRY_CODE+phoneNumber)

        docReference.get()
            .addOnCompleteListener { task ->
                progressDialog.dismiss()

                if (task.isSuccessful) {
                    val documentSnapshot: DocumentSnapshot? = task.result
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        // Document with the provided phone number already exists
                        // Continue with the password changing process after OTP verification
                        val intent = Intent(this, VerifyNumberActivity::class.java).apply {
                            putExtra(phoneNumberKey, binding.etPhoneNum.editText?.text.toString())
                        }
                        intent.putExtra(Constant.IS_FORGET_PASSWORD, isForgetPass)
                        startActivity(intent)
                        finish()
                    } else {
                        // Document does not exist, phone number is not registered yet
                        Toast.makeText(
                            this, "Phone number is not registered yet", Toast.LENGTH_SHORT
                        ).show()

                    }
                } else {
                    // Handle network error
                    Toast.makeText(
                        this, "Network error: ${task.exception?.message}", Toast.LENGTH_SHORT
                    ).show()

                }
            }
    }

   /* private fun checkPhoneNumberExists(phoneNumber: String) {
        // Show the loading indicator before starting the Firestore operation
        initProgressDialog().show()
        // Check if the user exists with the provided phone number
        val firestore = FirebaseFirestore.getInstance()
        val userCollection = firestore.collection("user")
        val docReference = userCollection.document(Constant.COUNTRY_CODE+phoneNumber)

        docReference.get()
            .addOnCompleteListener { task ->
                progressDialog.dismiss()

                if (task.isSuccessful) {
                    val documentSnapshot: DocumentSnapshot? = task.result
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        // Document with the provided phone number already exists
                        Toast.makeText(this, "Phone number already registered", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        val intent = Intent(this, VerifyNumberActivity::class.java).apply {
                            putExtra(phoneNumberKey, binding.etPhoneNum.editText?.text.toString())
                        }
                        intent.putExtra(Constant.IS_FORGET_PASSWORD, isForgetPass)
                        startActivity(intent)
                        finish()
                        // Document does not exist, phone number is available
                        //  Toast.makeText(this, "Phone number is available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Handle network error
                    Toast.makeText(
                        this,
                        "Network error: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }*/
   private fun checkPhoneNumberExists(phoneNumber: String) {
       // Show the loading indicator before starting the Firestore operation
       initProgressDialog().show()
       // Check if the user exists with the provided phone number
       val firestore = FirebaseFirestore.getInstance()
       val userCollection = firestore.collection("user")
       val docReference = userCollection.document(Constant.COUNTRY_CODE + phoneNumber)

       docReference.get()
           .addOnCompleteListener { task ->
               progressDialog.dismiss()

               if (task.isSuccessful) {
                   val documentSnapshot: DocumentSnapshot? = task.result
                   if (documentSnapshot != null && documentSnapshot.exists()) {
                       // Document with the provided phone number already exists
                       val hasPassword = documentSnapshot.contains("password") &&
                               documentSnapshot.get("password") != null

                       if (hasPassword) {
                           Toast.makeText(this, "Phone number already registered", Toast.LENGTH_SHORT).show()
                       } else {
                           // Password field is not available, allow user to create an account
                           val intent = Intent(this, VerifyNumberActivity::class.java).apply {
                               putExtra(phoneNumberKey, binding.etPhoneNum.editText?.text.toString())
                           }
                           intent.putExtra(Constant.IS_FORGET_PASSWORD, isForgetPass)
                           startActivity(intent)
                           finish()
                       }
                   } else {
                        // Document does not exist, phone number is available
                       // Allow user to create an account
                       val intent = Intent(this, VerifyNumberActivity::class.java).apply {
                           putExtra(phoneNumberKey, binding.etPhoneNum.editText?.text.toString())
                       }
                       intent.putExtra(Constant.IS_FORGET_PASSWORD, isForgetPass)
                       startActivity(intent)
                       finish()
                   }
               } else {
                   // Handle network error
                   Toast.makeText(
                       this,
                       "Network error: ${task.exception?.message}",
                       Toast.LENGTH_SHORT
                   ).show()
               }
           }
   }

    private fun validateNumber() {
        if (binding.etPhoneNum.editText?.text.toString().isEmpty()) {
            binding.etPhoneNum.error = "Enter your Phone Number"
            binding.etPhoneNum.requestFocus()
            return
        }

        if (binding.etPhoneNum.editText?.text.toString().count() == 10) {
            binding.etPhoneNum.clearFocus()
            if (isForgetPass){
                checkPhoneNumberExistsForForgetPass(binding.etPhoneNum.editText?.text.toString())
            }else{
                checkPhoneNumberExists(binding.etPhoneNum.editText?.text.toString())
            }

        } else {
            Toast.makeText(this, "Enter 10 digit number", Toast.LENGTH_SHORT).show()
        }
    }

}