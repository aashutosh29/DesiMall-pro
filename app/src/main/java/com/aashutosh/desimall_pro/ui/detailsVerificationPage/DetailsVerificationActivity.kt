package com.aashutosh.desimall_pro.ui.detailsVerificationPage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.databinding.ActivityDetailsVerificationBinding
import com.aashutosh.desimall_pro.ui.HomeActivity
import com.aashutosh.desimall_pro.utils.Constant
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DetailsVerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsVerificationBinding
    private lateinit var sharedPrefHelper: SharedPrefHelper
    private lateinit var progressDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun loadPreviousData() {
        binding.etEmail.setText(sharedPrefHelper[Constant.EMAIL, ""])
        binding.etPin.setText(sharedPrefHelper[Constant.ZIP, ""])
        binding.etName.setText(sharedPrefHelper[Constant.NAME, ""])
        binding.etAddress.setText(sharedPrefHelper[Constant.ADDRESS, ""])
        binding.etLandMark.setText(sharedPrefHelper[Constant.LAND_MARK, ""])
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

    private fun initView() {
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
    }
}