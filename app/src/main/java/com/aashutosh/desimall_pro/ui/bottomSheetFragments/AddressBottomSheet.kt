package com.aashutosh.desimall_pro.ui.bottomSheetFragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.databinding.BottomSheetAddressBinding
import com.aashutosh.desimall_pro.ui.HomeActivity
import com.aashutosh.desimall_pro.utils.Constant
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AddressBottomSheet : BottomSheetDialogFragment() {

    private lateinit var sharedPrefHelper: SharedPrefHelper
    lateinit var binding: BottomSheetAddressBinding
    private lateinit var progressDialog: AlertDialog

    companion object {
        fun newInstance(): AddressBottomSheet {
            return AddressBottomSheet()
        }
    }

    override fun getTheme() = R.style.CustomBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAddressBinding.inflate(inflater, container, false)
        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(requireContext())
        this.isCancelable = false
        return binding.root
    }

    private fun validateData(): Boolean {
        if (!binding.etName.text.toString().trim().contains(" ") || binding.etName.text.toString()
                .trim().length < 5
        ) {
            Toast.makeText(
                requireContext(),
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
                requireContext(),
                "Email address must be valid or should not empty",
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else if (binding.etAddress.text.toString()
                .trim().length < 5
        ) {
            Toast.makeText(
                requireContext(),
                "Enter your Full Address",
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else if (binding.etLandMark.text.toString()
                .trim().length < 5
        ) {
            Toast.makeText(
                requireContext(),
                "Enter your landmark address",
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else if (binding.etPin.text.toString()
                .trim().length < 4
        ) {
            Toast.makeText(
                requireContext(),
                "Enter a valid Pin code",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }


    private fun initProgressDialog(): AlertDialog {
        progressDialog = Constant.setProgressDialog(requireContext(), "Adding Details")
        progressDialog.setCancelable(false) // blocks UI interaction
        return progressDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvSkip.setOnClickListener(View.OnClickListener {
            sharedPrefHelper[Constant.USER_SKIPPED] = true
            val i = Intent(requireActivity(), HomeActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        })

        binding.btDetailsConfirm.setOnClickListener(View.OnClickListener {
            if (validateData()) {
                initProgressDialog().show()
                val db = Firebase.firestore
                val createUser = hashMapOf(
                    "name" to binding.etName.text.toString(),
                    "email" to binding.etEmail.text.toString(),
                    "zip" to binding.etPin.text.toString(),
                    "location" to binding.etAddress.text.toString(),
                    "landmark" to binding.etLandMark.text.toString()
                )

                db.collection("user").document(sharedPrefHelper[Constant.PHONE_NUMBER])
                    .update(createUser as Map<String, Any>).addOnSuccessListener {
                        sharedPrefHelper[Constant.DETAIlS_VERIFED] = true
                        sharedPrefHelper[Constant.EMAIL] = binding.etEmail.text.toString()
                        sharedPrefHelper[Constant.ZIP] = binding.etPin.text.toString()
                        sharedPrefHelper[Constant.NAME] = binding.etName.text.toString()
                        sharedPrefHelper[Constant.ADDRESS] = binding.etAddress.text.toString()
                        sharedPrefHelper[Constant.LAND_MARK] = binding.etLandMark.text.toString()
                        Toast.makeText(requireContext(), "Details Added", Toast.LENGTH_SHORT)
                            .show()
                        progressDialog.dismiss()
                        val i = Intent(requireActivity(), HomeActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(i)
                    }.addOnFailureListener {
                        progressDialog.dismiss()
                        Toast.makeText(
                            requireContext(),
                            "Unable to Add details. Try again later",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
            }

        })

    }
}