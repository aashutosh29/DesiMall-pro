package com.aashutosh.desimall_pro.ui.detailsVerificationPage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.databinding.ActivityDetailsVerificationBinding
import com.aashutosh.desimall_pro.models.newapi.LoginData
import com.aashutosh.desimall_pro.models.newapi.UserData
import com.aashutosh.desimall_pro.ui.HomeActivity
import com.aashutosh.desimall_pro.ui.phoneVerification.EnterNumberActivity
import com.aashutosh.desimall_pro.utils.Constant
import com.aashutosh.desimall_pro.viewModels.AuthenticationViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsVerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsVerificationBinding
    private lateinit var viewModel: AuthenticationViewModel

    private lateinit var sharedPrefHelper: SharedPrefHelper
    private lateinit var progressDialog: AlertDialog
    private lateinit var password : String
    private lateinit var timestamp : String
    private  var register : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        checkToSignInOrSignUP()
    }
    private fun initView() {
        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(this)
        viewModel = ViewModelProvider(this@DetailsVerificationActivity)[AuthenticationViewModel::class.java]
        viewModel.signUpResponse.observe(this) { signUpResponse ->
            signUpResponse?.let {

                if (signUpResponse.statusCode== 200){
                sharedPrefHelper[Constant.DETAILIlS_VERIFIED] = true
                sharedPrefHelper[Constant.NAME] = binding.etName.text.toString()
                    /*details form server*/
                    sharedPrefHelper[Constant.EMAIL] = signUpResponse.data.email
                    sharedPrefHelper[Constant.ZIP] = signUpResponse.data.pin
                    sharedPrefHelper[Constant.NAME] = signUpResponse.data.firstName+ " " +signUpResponse.data.lastName
                    sharedPrefHelper[Constant.ADDRESS] = signUpResponse.data.userAddress
                    sharedPrefHelper[Constant.LAND_MARK] = signUpResponse.data.state

                Toast.makeText(this, "User Details added successfully.", Toast.LENGTH_SHORT)
                    .show()
                progressDialog.dismiss()
                val i = Intent(this@DetailsVerificationActivity, HomeActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
            }else{
                    progressDialog.dismiss()
                    Toast.makeText(this, "Some Error Occurred. Please Try Again", Toast.LENGTH_SHORT)
                        .show()
            }
            }?: run {
                progressDialog.dismiss()
                Toast.makeText(this, "Some Error Occurred. Please Try Again", Toast.LENGTH_SHORT)
                    .show()
                // Handle the case when signUpResponse is null
                // Update your view accordingly
            }

        }
        viewModel.loginResponse.observe(this) { response ->
            response?.let {
                if (response.statusCode == 200){
                    sharedPrefHelper[Constant.DETAILIlS_VERIFIED] = true
                    sharedPrefHelper[Constant.NAME] = binding.etName.text.toString()

                    /*details form server*/
                    sharedPrefHelper[Constant.EMAIL] = response.data!!.email
                    sharedPrefHelper[Constant.ZIP] = response.data.pin
                    sharedPrefHelper[Constant.NAME] = response.data.firstName+ " " +response.data.lastName
                    sharedPrefHelper[Constant.ADDRESS] = response.data.userAddress
                    sharedPrefHelper[Constant.LAND_MARK] = response.data.state

                    Toast.makeText(this, "User Details fetched successfully.", Toast.LENGTH_SHORT)
                        .show()
                    progressDialog.dismiss()
                    val i = Intent(this@DetailsVerificationActivity, HomeActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)

                }
                else{
                    Toast.makeText(this, "Something went wrong. Please try Again later", Toast.LENGTH_SHORT)
                        .show()
                    progressDialog.dismiss()
                    val i = Intent(this@DetailsVerificationActivity, EnterNumberActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)
                }

            }
                ?: run {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Some Error Occurred. Please Try Again", Toast.LENGTH_SHORT)
                        .show()
                    val i = Intent(this@DetailsVerificationActivity, EnterNumberActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)
                    // Handle the case when signUpResponse is null
                    // Update your view accordingly
                }
        }
        binding.tvSkip.setOnClickListener(View.OnClickListener {
            sharedPrefHelper[Constant.USER_SKIPPED] = true
            val i = Intent(this@DetailsVerificationActivity, HomeActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        })

    }


    private fun checkToSignInOrSignUP() {
        if (sharedPrefHelper[Constant.PASSWORD,""].toString().isEmpty()){
            createPassword()
            binding.btDetailsConfirm.setOnClickListener(View.OnClickListener {
                if (validateData()) {
                    initProgressDialog().show()
                    val userData = UserData(
                        firstName = binding.etName.text.toString().split(" ")[0],
                        lastName = binding.etName.text.toString().split(" ")[1],
                        userAddress = binding.etAddress.text.toString(),
                        city = "unknown",
                        pin = binding.etPin.text.toString(),
                        state = binding.etLandMark.text.toString(),
                        country = "india",
                        mobile = sharedPrefHelper[Constant.PHONE_NUMBER],
                        dob = "0000-00-00",
                        password = timestamp,
                        cPassword = timestamp,
                        email = binding.etEmail.text.toString()
                    )
                    val db = Firebase.firestore
                    val createUser = hashMapOf(
                        "password" to timestamp,
                        "name" to binding.etName.text.toString(),
                        "branchCode" to sharedPrefHelper[Constant.BRANCH_CODE, ""],
                        "branchName" to sharedPrefHelper[Constant.BRANCH_NAME, ""]
                    )

                    db.collection("user").document(sharedPrefHelper[Constant.PHONE_NUMBER])
                        .update(createUser as Map<String, Any>).addOnSuccessListener {
                            sharedPrefHelper[Constant.PASSWORD] = timestamp
                            viewModel.signUp(userData)
                        }.addOnFailureListener {
                            progressDialog.dismiss()
                            Toast.makeText(
                                this,
                                "Unable to Add details. Try again later",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                }

            })

        }
        else{
            initProgressDialog().show()
            proceedSignIn()
        }
    }

    private fun loadPreviousData() {
        binding.etName.setText(sharedPrefHelper[Constant.NAME, ""])
        password = sharedPrefHelper[Constant.PASSWORD, ""]
    }

    private fun proceedSignIn() {
        loadPreviousData()
        val loginData = LoginData(sharedPrefHelper[Constant.PHONE_NUMBER, ""], password)
        // Call the login method in the ViewModel
        viewModel.login(loginData)

    }

    private fun createPassword() {
         timestamp = System.currentTimeMillis().toString()
    }


    private fun validateData(): Boolean {
        if (!binding.etName.text.toString().trim().contains(" ") || binding.etName.text.toString()
                .trim().length < 5
        ) {
            Toast.makeText(
                this,
                "Enter your Full Name",
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else if (!binding.etEmail.text.toString().trim()
                .contains("@") || !binding.etEmail.text.toString().trim()
                .contains(".") || binding.etEmail.text.toString()
                .trim().length < 7
        ) {
            Toast.makeText(
                this,
                "Email address must be valid or should not empty",
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else if (binding.etAddress.text.toString()
                .trim().length < 5
        ) {
            Toast.makeText(
                this,
                "Enter your Full Address",
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else if (binding.etLandMark.text.toString()
                .trim().length < 5
        ) {
            Toast.makeText(
                this,
                "Enter your landmark address",
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else if (binding.etPin.text.toString()
                .trim().length < 4
        ) {
            Toast.makeText(
                this,
                "Enter a valid Pin code",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }


    private fun initProgressDialog(): AlertDialog {
        progressDialog = Constant.setProgressDialog(this, "Adding Details")
        progressDialog.setCancelable(false) // blocks UI interaction
        return progressDialog
    }






     /*   private fun initView() {
        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(this)
        binding.tvSkip.setOnClickListener(View.OnClickListener {
            sharedPrefHelper[Constant.USER_SKIPPED] = true
            val i = Intent(this@DetailsVerificationActivity, HomeActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        })
        loadPreviousData()
        binding.btDetailsConfirm.setOnClickListener(View.OnClickListener {
            if (validateData()) {
                initProgressDialog().show()
                val db = Firebase.firestore
                val createUser = hashMapOf(
                    "name" to binding.etName.text.toString(),
                    "email" to binding.etEmail.text.toString(),
                    "zip" to binding.etPin.text.toString(),
                    "location" to binding.etAddress.text.toString(),
                    "landmark" to binding.etLandMark.text.toString(),
                    "branchCode" to sharedPrefHelper[Constant.BRANCH_CODE, ""],
                    "branchName" to sharedPrefHelper[Constant.BRANCH_NAME, ""]
                )

                db.collection("user").document(sharedPrefHelper[Constant.PHONE_NUMBER])
                    .update(createUser as Map<String, Any>).addOnSuccessListener {
                        sharedPrefHelper[Constant.DETAILIlS_VERIFIED] = true
                        sharedPrefHelper[Constant.EMAIL] = binding.etEmail.text.toString()
                        sharedPrefHelper[Constant.ZIP] = binding.etPin.text.toString()
                        sharedPrefHelper[Constant.NAME] = binding.etName.text.toString()
                        sharedPrefHelper[Constant.ADDRESS] = binding.etAddress.text.toString()
                        sharedPrefHelper[Constant.LAND_MARK] = binding.etLandMark.text.toString()
                        Toast.makeText(this, "Details Added", Toast.LENGTH_SHORT)
                            .show()
                        progressDialog.dismiss()
                        val i = Intent(this@DetailsVerificationActivity, HomeActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(i)
                    }.addOnFailureListener {
                        progressDialog.dismiss()
                        Toast.makeText(
                            this,
                            "Unable to Add details. Try again later",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
            }

        })
    }*/
}