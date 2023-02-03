package com.aashutosh.desimall_pro.ui.phoneVerification

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.databinding.ActivityVerifyNumberBinding
import com.aashutosh.desimall_pro.ui.HomeActivity
import com.aashutosh.desimall_pro.ui.detailsVerificationPage.DetailsVerificationActivity
import com.aashutosh.desimall_pro.utils.Constant
import com.aashutosh.desimall_pro.utils.Constant.Companion.phoneNumberKey
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class VerifyNumberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerifyNumberBinding


    private lateinit var auth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    lateinit var sharedPrefHelper: SharedPrefHelper

    private lateinit var progressDialog: AlertDialog
    private var phoneNum: String = "+91"
    private var storedVerificationId: String? = null
    private val TAG = "VerifyNumberActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        auth.useAppLanguage()
        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(this)


        if (intent != null) {
            val num = intent.getStringExtra(phoneNumberKey).toString()
            phoneNum += num
            Log.d(TAG, phoneNum)
            "Authenticate $phoneNum".also { binding.textAuthenticateNum.text = it }
        } else {
            Toast.makeText(this, "Bad Gateway 😒", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnVerify.setOnClickListener {
            if (binding.etOtp.editText?.text.toString().isNotEmpty()) {
                binding.etOtp.clearFocus()
                verifyVerificationCode(binding.etOtp.editText?.text.toString())
            } else {
                binding.etOtp.error = "Enter OTP 🤨"
                binding.etOtp.requestFocus()
                return@setOnClickListener
            }
        }

        binding.btnCancel.setOnClickListener {
            returnToEnterNumberActivity()
        }


        verificationCallbacks()

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNum)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun initProgressDialog(): AlertDialog {
        progressDialog = Constant.setProgressDialog(this, "Registering User")
        progressDialog.setCancelable(false) // blocks UI interaction
        return progressDialog
    }

    private fun verificationCallbacks() {

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")

                val code = credential.smsCode
                if (code != null) {
                    verifyVerificationCode(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        // Invalid request
                        Toast.makeText(
                            this@VerifyNumberActivity,
                            "Invalid request", Toast.LENGTH_SHORT
                        ).show()
                        returnToEnterNumberActivity()

                    }
                    is FirebaseTooManyRequestsException -> {
                        // The SMS quota for the project has been exceeded
                        Toast.makeText(
                            this@VerifyNumberActivity,
                            "The SMS quota for the project has been exceeded",
                            Toast.LENGTH_SHORT
                        ).show()
                        returnToEnterNumberActivity()

                    }
                    else -> {
                        Log.d(TAG, "newNew: " + e.message.toString())
                        // Show a message and update the UI
                        Toast.makeText(
                            this@VerifyNumberActivity,
                            e.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        returnToEnterNumberActivity()
                    }
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token

                Toast.makeText(
                    this@VerifyNumberActivity,
                    "OTP sent to $phoneNum",
                    Toast.LENGTH_SHORT
                ).show()

                super.onCodeSent(verificationId, resendToken)

            }
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    initProgressDialog().show()
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val db = Firebase.firestore
                    val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                    val date = Date()
                    val createUser = hashMapOf(
                        "phone" to task.result?.user?.phoneNumber,
                        "date" to dateFormat.format(date),
                    )



                    db.collection("user").whereEqualTo("phone", task.result?.user?.phoneNumber!!)
                        .limit(1).get().addOnCompleteListener {
                            if (it.result.isEmpty) {
                                db.collection("user").document(task.result?.user?.phoneNumber!!)
                                    .set(createUser).addOnSuccessListener {
                                        Toast.makeText(
                                            this, "Authorization Completed 🥳🥳", Toast.LENGTH_SHORT
                                        ).show()
                                        sharedPrefHelper[Constant.VERIFIED_NUM] = true
                                        sharedPrefHelper[Constant.PHONE_NUMBER] =
                                            task.result?.user?.phoneNumber?.trim()
                                        progressDialog.dismiss()
                                        val i = Intent(
                                            this@VerifyNumberActivity,
                                            DetailsVerificationActivity::class.java
                                        )
                                        i.flags =
                                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                        i.putExtra(Constant.VERIFY_USER_LOCATION, true)
                                        startActivity(i)
                                        finish()
                                    }.addOnFailureListener {
                                        progressDialog.dismiss()
                                        Toast.makeText(
                                            this, "User Not Created Retry Again", Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                sharedPrefHelper[Constant.PHONE_NUMBER] =
                                    task.result?.user?.phoneNumber?.trim()
                                sharedPrefHelper[Constant.VERIFIED_NUM] = true
                                progressDialog.dismiss()
                                sharedPrefHelper[Constant.EMAIL] =
                                    it.result.documents[0].data!!["email"].toString()
                                sharedPrefHelper[Constant.ZIP] =
                                    it.result.documents[0].data!!["zip"].toString()
                                sharedPrefHelper[Constant.NAME] =
                                    it.result.documents[0].data!!["name"].toString()
                                sharedPrefHelper[Constant.ADDRESS] =
                                    it.result.documents[0].data!!["location"].toString()
                                sharedPrefHelper[Constant.LAND_MARK] =
                                    it.result.documents[0].data!!["landmark"].toString()
                                sharedPrefHelper[Constant.PHOTO] =
                                    it.result.documents[0].data!!["photo"].toString()
                                sharedPrefHelper[Constant.DETAILIlS_VERIFIED] = true

                                val i = Intent(
                                    this@VerifyNumberActivity, HomeActivity::class.java
                                )
                                i.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(i)


                            }
                        }
                        .addOnFailureListener {
                            Log.d(TAG, "signInWithPhoneAuthCredential: $it")
                        }


                } else {

                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(
                            this,
                            "The verification code entered was invalid 🥺",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Update UI
                        Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                    returnToEnterNumberActivity()

                }
            }
    }

    private fun verifyVerificationCode(code: String) {

        //creating the credential
        val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
        //signing the user
        signInWithPhoneAuthCredential(credential)

    }

    private fun returnToEnterNumberActivity() {
        val intent = Intent(applicationContext, EnterNumberActivity::class.java)
        startActivity(intent)
        finish()
    }
}