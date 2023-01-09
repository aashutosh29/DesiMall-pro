package com.aashutosh.desimall_pro.ui.myProfileActivity


import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.MotionEffect
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.databinding.ActivityMyProfileBinding
import com.aashutosh.desimall_pro.ui.mapActivity.MapsActivity
import com.aashutosh.desimall_pro.utils.Constant
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kroegerama.imgpicker.BottomSheetImagePicker
import com.kroegerama.imgpicker.ButtonType
import id.zelory.compressor.Compressor
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class MyProfileActivity : AppCompatActivity(), BottomSheetImagePicker.OnImagesSelectedListener {
    lateinit var binding: ActivityMyProfileBinding
    lateinit var sharedPrefHelper: SharedPrefHelper
    var uriArrayList: ArrayList<Uri> = arrayListOf()
    var imageList: java.util.ArrayList<String> = arrayListOf()
    private lateinit var progressDialog: AlertDialog
    private lateinit var firebaseStorage: FirebaseStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(this)
        binding.tvName.text = sharedPrefHelper[Constant.NAME, ""]
        binding.tvMail.text = sharedPrefHelper[Constant.EMAIL, ""]
        binding.tvPhoneNumber.text = sharedPrefHelper[Constant.PHONE_NUMBER, ""]
        binding.tvAddress.text = sharedPrefHelper[Constant.ADDRESS, ""]
        binding.tvLandMark.text = sharedPrefHelper[Constant.LAND_MARK, ""]
        binding.tvEditLocation.setOnClickListener(View.OnClickListener {
            val i = Intent(this@MyProfileActivity, MapsActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            i.putExtra(Constant.IS_PROFILE, true)
            startActivity(i)
        })
        binding.ivBack.setOnClickListener(View.OnClickListener {
            this.finish()
        })

        Glide.with(this@MyProfileActivity)
            .load(sharedPrefHelper[Constant.PHOTO, ""])
            .error(R.drawable.ic_baseline_lock_24)
            .into(binding.ivImage)

        binding.tvEditPhoto.setOnClickListener(View.OnClickListener {
            BottomSheetImagePicker.Builder(getString(R.string.file_provider))
                .cameraButton(ButtonType.Button)            //style of the camera link (Button in header, Image tile, None)
                .galleryButton(ButtonType.Button)           //style of the gallery link
                .singleSelectTitle(R.string.pick_single)    //header text
                .peekHeight(R.dimen.peekHeight)             //peek height of the bottom sheet
                .columnSize(R.dimen.columnSize)             //size of the columns (will be changed a little to fit)
                .requestTag("single")                       //tag can be used if multiple pickers are used
                .show(supportFragmentManager)
        })
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onImagesSelected(uris: List<Uri>, tag: String?) {
        initProgressDialog().show()
        uriArrayList = arrayListOf()

        uris.forEach { uri ->
            this.uriArrayList.add(uri)
        }

        GlobalScope.launch {
            uploadImage(
                Compressor.compress(
                    this@MyProfileActivity,
                    File(getRealPathFromUri(this@MyProfileActivity, uriArrayList[0])!!)
                ).toUri()
            )
        }
    }

    private fun getRealPathFromUri(context: Context, contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
            val columnIndex: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } finally {
            cursor?.close()
        }
    }

    private fun initProgressDialog(): AlertDialog {
        progressDialog = Constant.setProgressDialog(this, "Uploading Profile Image")
        progressDialog.setCancelable(false) // blocks UI interaction
        return progressDialog
    }


    private fun uploadImage(uri: Uri) {
        val tsLong = System.currentTimeMillis() / 1000
        val ts = tsLong.toString()
        firebaseStorage = FirebaseStorage.getInstance()
        val uploader: StorageReference =
            firebaseStorage.reference.child("real_state_${sharedPrefHelper[Constant.PHONE_NUMBER, ""]}_$ts")

        uploader.putFile(uri).addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnCompleteListener { task ->
                val fileLink = task.result.toString()
                imageList.add(fileLink)
                Log.d(ContentValues.TAG, "uploadImage: $imageList")
                Log.d(MotionEffect.TAG, "fileLink: $fileLink")

                val db = Firebase.firestore
                val createUser = hashMapOf(
                    "photo" to imageList[0],
                )
                db.collection("user").document(sharedPrefHelper[Constant.PHONE_NUMBER])
                    .update(createUser as Map<String, Any>).addOnSuccessListener {
                        sharedPrefHelper[Constant.PHOTO] = imageList[0]

                        progressDialog.dismiss()
                        Glide.with(this@MyProfileActivity)
                            .load(imageList[0])
                            .error(R.drawable.ic_baseline_lock_24)
                            .into(binding.ivImage)
                        Toast.makeText(
                            this@MyProfileActivity,
                            "Image Added",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        progressDialog.dismiss()

                    }.addOnFailureListener {
                        progressDialog.dismiss()
                        Toast.makeText(
                            this@MyProfileActivity,
                            "Unable to Add photo. Try again later",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
            }.addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(
                    this@MyProfileActivity, it.toString(), Toast.LENGTH_SHORT
                ).show()
            }
        }.addOnFailureListener {
            progressDialog.dismiss()
            Toast.makeText(
                this@MyProfileActivity, it.toString(), Toast.LENGTH_SHORT
            ).show()

        }
    }



    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}