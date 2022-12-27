package com.aashutosh.simplestore.ui.onBoarding


import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.aashutosh.simplestore.R
import com.aashutosh.simplestore.database.SharedPrefHelper
import com.aashutosh.simplestore.ui.HomeActivity
import com.aashutosh.simplestore.ui.profile.SignIn
import com.aashutosh.simplestore.ui.profile.SignUp
import com.aashutosh.simplestore.utils.Constant
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


open class OnboardFinishActivity : AppCompatActivity() {
    private lateinit var clGoogle: ConstraintLayout
    lateinit var progressDialog: AlertDialog
    private lateinit var callbackManager: CallbackManager
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val reqCode: Int = 123
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPrefHelper: SharedPrefHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_finish)
        initView()
        FirebaseApp.initializeApp(this)
        firebaseAuth = FirebaseAuth.getInstance()
        googleInit()
        facebookInit()
        initProgressDialog()
        get_hash_key()
    }

    private fun facebookInit() {
        val clFb = findViewById<LoginButton>(R.id.clFb)
        callbackManager = CallbackManager.Factory.create()
        clFb.setReadPermissions("email", "public_profile")
        clFb.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
                // ...
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
                // ...
            }
        })
    }

    private fun googleInit() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        clGoogle.setOnClickListener(View.OnClickListener {
            BeginSignInRequest.builder()
            signInGoogle()
        })
    }

    open fun get_hash_key() {
        val info: PackageInfo
        try {
            info = packageManager.getPackageInfo(
                "com.keybrains.simplestore",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                var md: MessageDigest
                md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val something: String = String(Base64.encode(md.digest(), 0))
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something)
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            Log.e("name not found", e1.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("no such an algorithm", e.toString())
        } catch (e: Exception) {
            Log.e("exception", e.toString())
        }
    }

    private fun initView() {
        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(this)
        val btSkip = findViewById<AppCompatButton>(R.id.btSkip)
        val clSignIn = findViewById<ConstraintLayout>(R.id.clSignIn)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)
        clGoogle = findViewById<ConstraintLayout>(R.id.clGoogle)
        clSignIn.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@OnboardFinishActivity, SignIn::class.java)
            startActivity(intent)
        })
        btSkip.setOnClickListener {
            val intent = Intent(this@OnboardFinishActivity, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

        }
        tvRegister.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@OnboardFinishActivity, SignUp::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

        })


    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                    if (task.isSuccessful) {
                        if (user != null) {
                            sharedPrefHelper[Constant.LOGIN] = true
                            sharedPrefHelper[Constant.NAME] = user.displayName.toString()
                            sharedPrefHelper[Constant.EMAIL] = user.email.toString()
                            sharedPrefHelper[Constant.PHOTO] = user.photoUrl.toString()
                            Log.d(
                                TAG,
                                "UpdateUI: " + user.email.toString() + "  " + user.displayName.toString() + user.photoUrl
                            )
                        }
                        // SavedPreference.setEmail(this, account.email.toString())
                        //  SavedPreference.setUsername(this, account.displayName.toString())
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
    }

    fun signInGoogle() {
        initProgressDialog()
        progressDialog.show()
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, reqCode)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == reqCode) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                updateUi(account)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun initProgressDialog(): AlertDialog {
        progressDialog = setProgressDialog(this, "signing please wait..")
        progressDialog.setCancelable(false) // blocks UI interaction
        return progressDialog
    }

    private fun updateUi(account: GoogleSignInAccount) {
        initProgressDialog().dismiss()
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                sharedPrefHelper[Constant.LOGIN] = true
                sharedPrefHelper[Constant.NAME] = account.displayName.toString()
                sharedPrefHelper[Constant.EMAIL] = account.email.toString()
                sharedPrefHelper[Constant.PHOTO] = account.photoUrl.toString()
                Log.d(
                    TAG,
                    "UpdateUI: " + account.email.toString() + "  " + account.displayName.toString() + account.photoUrl
                )
                Toast.makeText(this, "Sign in successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setProgressDialog(context: Context, message: String): AlertDialog {
        val llPadding = 30
        val ll = LinearLayout(context)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        ll.gravity = Gravity.START
        var llParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.START
        ll.layoutParams = llParam

        val progressBar = ProgressBar(context)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, llPadding, 0)
        progressBar.layoutParams = llParam

        llParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.CENTER
        val tvText = TextView(context)
        tvText.text = message
        tvText.setTextColor(Color.parseColor("#000000"))
        tvText.textSize = 16.toFloat()
        tvText.layoutParams = llParam

        ll.addView(progressBar)
        ll.addView(tvText)

        val builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setView(ll)

        val dialog = builder.create()
        val window = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams
        }
        return dialog
    }
}
