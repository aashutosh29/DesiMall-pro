package com.aashutosh.desimall_pro.ui.detailsVerificationPage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Switch
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.databinding.ActivityDetailsVerificationBinding
import com.aashutosh.desimall_pro.sealed.StateResponse
import com.aashutosh.desimall_pro.ui.HomeActivity
import com.aashutosh.desimall_pro.ui.RequestForFetchingLocationActivity
import com.aashutosh.desimall_pro.utils.Constant
import com.aashutosh.desimall_pro.viewModels.LocationViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsVerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsVerificationBinding
    private lateinit var sharedPrefHelper: SharedPrefHelper
    private lateinit var progressDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(this)
        loadPreviousData()
        goToUserInput()
        val locationViewModel: LocationViewModel by viewModels()

        val latLongPair = getLatitudeAndLongitude(sharedPrefHelper[Constant.LAT_LON, ""])

        if (latLongPair != null) {
            val latitude = latLongPair.first
            val longitude = latLongPair.second
            locationViewModel.fetchLocation(latitude, longitude)
        // Observe the location response
        locationViewModel.locationResponse.observe(this) { stateResponse ->
            when (stateResponse) {
                is StateResponse.Loading -> {
                    initProgressDialog().show()
                }
                is StateResponse.Success -> {
                    progressDialog.dismiss()
                    val locationData = stateResponse.data
                    if (locationData != null) {
                        val city = locationData.address.state_district
                        val state = locationData.address.state
                        val addressDetails = locationData.display_name
                        val postCode = locationData.address.postcode

                        binding.etCity.setText(city)
                        binding.etState.setText(state)
                        binding.etAddressDetails.setText(addressDetails)
                        binding.etPin.setText(postCode)
                    }else{
                        Toast.makeText(
                            this,
                            "(Null) Unable to Load data",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    // Use the locationData to update your UI or perform other operations
                }
                is StateResponse.Error -> {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this,
                        "Unable to Load data",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
        }

        initView()
    }

    private  fun switchToAskVerification(){
        binding.svMain.visibility = View.GONE
        binding.llAskUser.visibility = View.VISIBLE
    }
    private fun goToUserInput(){
        binding.svMain.visibility = View.VISIBLE
        binding.llAskUser.visibility = View.GONE
    }


    private fun loadPreviousData() {
        binding.etEmail.setText(sharedPrefHelper[Constant.EMAIL, ""])
        binding.etPin.setText(sharedPrefHelper[Constant.ZIP, ""])
        binding.etName.setText(sharedPrefHelper[Constant.NAME, ""])
        binding.etAddress.setText(sharedPrefHelper[Constant.ADDRESS, ""])
        binding.etLandMark.setText(sharedPrefHelper[Constant.LAND_MARK, ""])
        /*newly added*/
        binding.etCity.setText(sharedPrefHelper[Constant.USER_CITY,""])
        binding.etState.setText(sharedPrefHelper[Constant.USER_STATE,""])
        binding.etAddressDetails.setText(sharedPrefHelper[Constant.ADDRESS_FULL_DETAILS,""])

    }
    private fun validateData(): Boolean {
        if (!binding.etName.text.toString().trim().contains(" ") || binding.etName.text.toString().trim().length < 5) {
            Toast.makeText(this, "Enter your Full Name", Toast.LENGTH_SHORT).show()
            return false
        } else if (!binding.etEmail.text.toString().trim().contains("@") || !binding.etEmail.text.toString().trim().contains(".") || binding.etEmail.text.toString().trim().length < 7) {
            Toast.makeText(this, "Email address must be valid or should not be empty", Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.etAddress.text.toString().trim().length < 5) {
            Toast.makeText(this, "Enter your Full Address", Toast.LENGTH_SHORT).show()
            return false
        }  else if (binding.etPin.text.toString().trim().length < 4) {
            Toast.makeText(this, "Enter a valid Pin code", Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.etCity.text.toString().trim().length < 3) {
            Toast.makeText(this, "Enter a valid City", Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.etState.text.toString().trim().length < 3) {
            Toast.makeText(this, "Enter a valid State", Toast.LENGTH_SHORT).show()
            return false
        }
        else if (binding.etAddressDetails.text.toString().trim().length < 3) {
            Toast.makeText(this, "Enter a valid State", Toast.LENGTH_SHORT).show()
            return false
        }

        return true

    }


    private fun initProgressDialog(): AlertDialog {
        progressDialog = Constant.setProgressDialog(this, "Adding Details")
        progressDialog.setCancelable(false) // blocks UI interaction
        return progressDialog
    }

    private fun getLatitudeAndLongitude(locationString: String): Pair<Double, Double>? {
        val latLongValues = locationString.split("_")

        if (latLongValues.size == 2) {
            val latitude = latLongValues[0].toDoubleOrNull()
            val longitude = latLongValues[1].toDoubleOrNull()

            if (latitude != null && longitude != null) {
                return Pair(latitude, longitude)
            }
        }

        return null
    }


    private fun initView() {
        binding.ivBack.setOnClickListener(View.OnClickListener {
            goToUserInput()
        })
        binding.btnModifyData.setOnClickListener(View.OnClickListener {
            goToUserInput()
        })

        binding.btnSignUP.setOnClickListener(View.OnClickListener {
            if (validateData()) {
                initProgressDialog().show()
                val db = Firebase.firestore
                val createUser = hashMapOf(
                    "name" to binding.etName.text.toString(),
                    "email" to binding.etEmail.text.toString(),
                    "zip" to binding.etPin.text.toString(),
                    "location" to binding.etAddress.text.toString(),
                    "landmark" to "n-a",
                    "fullAddress" to binding.etAddressDetails.text.toString(),
                    "city" to binding.etCity.text.toString(),
                    "state" to binding.etState.text.toString(),
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
                        sharedPrefHelper[Constant.USER_CITY] = binding.etCity.text.toString()
                        sharedPrefHelper[Constant.USER_STATE] = binding.etState.text.toString()
                        sharedPrefHelper[Constant.ADDRESS_FULL_DETAILS] = binding.etAddressDetails.text.toString()
                        Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT)
                            .show()
                        progressDialog.dismiss()
                        val i = Intent(this@DetailsVerificationActivity, HomeActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(i)
                    }.addOnFailureListener {
                        progressDialog.dismiss()
                        Toast.makeText(
                            this,
                            "Unable to Create Account. Try again later",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
            }
        })
        binding.btDetailsConfirm.setOnClickListener(View.OnClickListener {
            if (validateData()){
                binding.tvEmailValue.text = binding.etEmail.text.toString()
                binding.tvPinValue.text = binding.etPin.text.toString()
                binding.tvNameValue.text = binding.etName.text.toString()
                binding.tvAddressValue.text = binding.etAddress.text.toString()
                binding.tvCityValue.text = binding.etCity.text.toString()
                binding.tvStateValue.text = binding.etState.text.toString()
                binding.tvAddressDetailsValue.text = binding.etAddressDetails.text.toString()
                switchToAskVerification()
            }
        })
    }
}