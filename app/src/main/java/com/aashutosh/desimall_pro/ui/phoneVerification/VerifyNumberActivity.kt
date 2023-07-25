package com.aashutosh.desimall_pro.ui.phoneVerification

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.databinding.ActivityVerifyNumberBinding
import com.aashutosh.desimall_pro.ui.LoginActivity
import com.aashutosh.desimall_pro.ui.PasswordActivity
import com.aashutosh.desimall_pro.utils.Constant
import com.aashutosh.desimall_pro.utils.Constant.Companion.phoneNumberKey

import com.aashutosh.desimall_pro.utils.SmsBroadCastReceiver
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

class VerifyNumberActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyNumberBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var sharedPrefHelper: SharedPrefHelper
    private lateinit var progressDialog: AlertDialog
    private var phoneNum: String = Constant.COUNTRY_CODE
    private var storedVerificationId: String? = null
    private val TAG = "VerifyNumberActivity"
    private var isForgetPass: Boolean = false


    private val REQUEST_USER_CONSENT = 200
    var smsBroadCastReceiver: SmsBroadCastReceiver? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        isForgetPass = intent.getBooleanExtra(Constant.IS_FORGET_PASSWORD, false)
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
                Log.d(TAG, "onCreate otp: ${binding.etOtp.editText?.text.toString()}")
                binding.etOtp.clearFocus()
                verifyVerificationCode(binding.etOtp.editText?.text.toString())
            } else {
                binding.etOtp.error = "Enter OTP 🤨"
                binding.etOtp.requestFocus()
                return@setOnClickListener
            }
        }

        verificationCallbacks()

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNum)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
      //  registerBroadCastReceiver()

      //  startSmartUserConsent()

        PhoneAuthProvider.verifyPhoneNumber(options)


    }

    private fun startSmartUserConsent() {
        val client = SmsRetriever.getClient(this)
        client.startSmsUserConsent(null)
    }

    private fun registerBroadCastReceiver() {
        smsBroadCastReceiver = SmsBroadCastReceiver()
        smsBroadCastReceiver!!.smsBroadCastReceiverListener =
            object : SmsBroadCastReceiver.SmsBroadCastReceiverListener {
                override fun onSuccess(otp: Intent?) {
                    startActivityForResult(intent, REQUEST_USER_CONSENT)
                }

                override fun onFailure() {
                    Toast.makeText(
                        this@VerifyNumberActivity,
                        "Error while fetching OTP",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsBroadCastReceiver, intentFilter)
    }


    override fun onStop() {
        super.onStop()
        if (smsBroadCastReceiver != null) {
            unregisterReceiver(smsBroadCastReceiver)
        }
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_USER_CONSENT) {
            if (resultCode == RESULT_OK && data != null) {
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                fillOTP(message)
            }
        }
    }*/

    private fun fillOTP(message: String?) {
        if (message != null) {
            val otpPattern = Regex("\\d{6}")
            val matchResult = otpPattern.find(message)
            val otp = matchResult?.value
            if (!otp.isNullOrBlank()) {
                binding.etOtp.editText?.setText(otp)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (smsBroadCastReceiver != null) {
            unregisterReceiver(smsBroadCastReceiver)
        }
    }

    private fun initProgressDialog(): AlertDialog {
        progressDialog = Constant.setProgressDialog(this, "Validating number")
        progressDialog.setCancelable(false) // blocks UI interaction
        return progressDialog
    }

    private fun verificationCallbacks() {

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
                val code = credential.smsCode
                if (code != null) {
                    verifyVerificationCode(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)
                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        // Invalid request
                        Toast.makeText(
                            this@VerifyNumberActivity,
                            "Invalid request", Toast.LENGTH_SHORT
                        ).show()
                        returnToLoginActivity()
                    }

                    is FirebaseTooManyRequestsException -> {
                        // The SMS quota for the project has been exceeded
                        Toast.makeText(
                            this@VerifyNumberActivity,
                            "The SMS quota for the project has been exceeded",
                            Toast.LENGTH_SHORT
                        ).show()
                        returnToLoginActivity()
                    }

                    else -> {
                        Log.d(TAG, "newNew: " + e.message.toString())
                        // Show a message and update the UI
                        Toast.makeText(
                            this@VerifyNumberActivity,
                            e.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        returnToLoginActivity()
                    }
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
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
                    sharedPrefHelper[Constant.PHONE_NUMBER] =
                        task.result?.user?.phoneNumber?.trim()
                    if (isForgetPass){
                        Toast.makeText(
                            this, "Authorized", Toast.LENGTH_SHORT
                        ).show()
                        val i = Intent(
                            this@VerifyNumberActivity,
                            PasswordActivity::class.java
                        )
                        i.putExtra(Constant.IS_FORGET_PASSWORD, isForgetPass)
                        i.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(i)
                        finish()
                    }
                    else{

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
                    db.collection("user").document(task.result?.user?.phoneNumber!!)
                        .set(createUser).addOnSuccessListener {
                            Toast.makeText(
                                this, "Authorized", Toast.LENGTH_SHORT
                            ).show()
                            sharedPrefHelper[Constant.PHONE_NUMBER] =
                                task.result?.user?.phoneNumber?.trim()
                            progressDialog.dismiss()
                            val i = Intent(
                                this@VerifyNumberActivity,
                                PasswordActivity::class.java
                            )
                            i.putExtra(Constant.IS_FORGET_PASSWORD, isForgetPass)
                            i.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(i)
                            finish()
                        }.addOnFailureListener {
                            progressDialog.dismiss()
                            Toast.makeText(
                                this, "User Not Created Retry Again", Toast.LENGTH_SHORT
                            ).show()
                        }
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
                    returnToLoginActivity()
                }
            }
    }

    private fun verifyVerificationCode(code: String) {
        //creating the credential
        val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
        //signing the user
        signInWithPhoneAuthCredential(credential)
    }

    private fun returnToLoginActivity() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}