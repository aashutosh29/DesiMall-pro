package com.aashutosh.desimall_pro.splash.test

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import butterknife.BindView
import butterknife.ButterKnife
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.models.java.Store
import com.aashutosh.desimall_pro.ui.SplashOldActivity
import com.aashutosh.desimall_pro.utils.Constant
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity2 : AppCompatActivity(), LocationListener {

    @BindView(R.id.pbMain)
    lateinit var pbMain: LinearProgressIndicator
    var db: FirebaseFirestore? = null
    var storeList: MutableList<Store>? = null
    var alertDialog: AlertDialog? = null
    lateinit var sharedPreferHelper: SharedPrefHelper




    var onStop = false
    var locationManager: LocationManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        ButterKnife.bind(this)
        sharedPreferHelper = SharedPrefHelper
        sharedPreferHelper.init(this)
        initialization()
        checkAllStuff()

    }

    private fun checkAllStuff() {
        if (serviceAvailable()) {
            if (alertDialog != null && alertDialog!!.isShowing) {
                alertDialog!!.dismiss()
            }
            if (!checkPermissions()) {
                requestPermissions()
            } else {
                location
            }
        } else {
            if (alertDialog != null && !alertDialog!!.isShowing) {
                alertDialog!!.show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (onStop) {
            if (alertDialog != null && alertDialog!!.isShowing) {
                alertDialog!!.dismiss()
            }
            initialization()
            checkAllStuff()
        }
    }

    override fun onStop() {
        super.onStop()
        onStop = true
    }

    private fun serviceAvailable(): Boolean {
        val lm = this.getSystemService(LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (_: Exception) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (_: Exception) {
        }
        return if (!gps_enabled && !network_enabled) {
            // notify user
            alertDialog =
                AlertDialog.Builder(this).setMessage("Location is turned off").setPositiveButton(
                    "Turn on location"
                ) { _, _ -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                    .setNegativeButton(
                        "Cancel"
                    )
                    //real web-view
                    { _, _ ->
                        startWebView(
                            Constant.BRANCH_NAME,
                            branchCode = "In order to use the application, you need to grant location permission. If you haven't done so already, please restart the app and provide us with the necessary location permission."
                        )
                    }.show()
            false
        } else {
            true
        }
    }

    private fun initialization() {
        if (alertDialog != null && alertDialog!!.isShowing) alertDialog!!.dismiss()
        db = FirebaseFirestore.getInstance()
        storeList = ArrayList()

    }

    private fun subscribeToTopic(
        notificationTopic: String,
        url: String,
        name: String,
        branchName: String,
        branchCode: String
    ) {
        FirebaseMessaging.getInstance().subscribeToTopic(notificationTopic).addOnCompleteListener(
            OnCompleteListener { task ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    pbMain.setProgress(80, true)
                } else {
                    pbMain.progress = 80
                }
                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        println("fetching failed")
                        startWebView(name, branchCode)
                    }
                    val token = task.result
                    Log.d("TAG", "onComplete: $token")
                    startWebView(name, branchCode)
                }).addOnFailureListener {
                    startWebView(
                        name,
                        branchCode
                    )
                }
                var msg = "Successful"
                if (!task.isSuccessful) {
                    msg = "Failed"
                }
            }).addOnFailureListener {
            startWebView(
                name,
                branchCode
            )
        }
    }

    private fun startWebView(branchName: String?, branchCode: String?) {
        sharedPreferHelper[Constant.BRANCH_NAME] = branchName
        val intent = Intent(this@SplashActivity2, SplashOldActivity::class.java)
        //        intent.putExtra(Constant.BRANCH_NAME, gmail);
        //        intent.putExtra(Constant.BRANCH_CODE, branchCode);
        intent.putExtra(Constant.BRANCH_NAME, "Gurugram - Hamilton Court")
        intent.putExtra(Constant.BRANCH_CODE, "6")
        //intent.putExtra(Constant.BRANCH_NAME, branchName)
       // intent.putExtra(Constant.BRANCH_CODE, branchCode)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun getDataFromFireBase(location: Location?) {
        if (locationManager != null) locationManager!!.removeUpdates(this)
        db!!.collection("stores").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                storeList = ArrayList()
                var store: Store
                for (document in task.result) {
                    store = Store(
                        document.id,
                        document.data["name"].toString(),
                        document.data["lat"].toString(),
                        document.data["long"].toString(),
                        document.data["url"].toString(),
                        document.data["notificationTopic"].toString(),
                        document.data["gmail"].toString(),
                        document.data["branchCode"].toString()

                    )
                    (storeList as ArrayList<Store>).add(store)
                    Log.d("proAss", document.id + " =>" + document.data)
                }
                fetchUrlAndSubscribeTopic(shortestStoreList(location))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    pbMain.setProgress(30, true)
                } else {
                    pbMain.progress = 30
                }
            } else {
                Log.w("Aashutosh", "Error getting documents.", task.exception)
            }
        }.addOnFailureListener {
            Log.d(TAG, "getDataFromFireBase:$it ")
            fetchUrlAndSubscribeTopic(ArrayList())
        }
    }

    private fun checkPermissions(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i("TAG", "Displaying permission rationale to provide additional context.")
            Snackbar.make(
                findViewById(R.id.clMain),
                R.string.permission_rationale,
                Snackbar.LENGTH_INDEFINITE
            ).setAction(
                R.string.ok
            ) { // Request permission
                ActivityCompat.requestPermissions(
                    this@SplashActivity2,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE
                )
            }.show()
        } else {
            Log.i("TAG", "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(
                this@SplashActivity2,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i(ContentValues.TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isEmpty()) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(ContentValues.TAG, "User interaction was cancelled.")
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                location
            } else {
                Snackbar.make(
                    findViewById(R.id.clMain),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(
                    R.string.ok
                ) { // Request permission
                    ActivityCompat.requestPermissions(
                        this@SplashActivity2,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSIONS_REQUEST_CODE
                    )
                }.show()
            }
        }
    }

    private val location: Unit
        get() {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Snackbar.make(
                    findViewById(R.id.clMain),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(
                    R.string.ok
                ) { requestPermissions() }.show()
            }
            //final change will be NETWORK_PROVIDER
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0L,
                0f,
                this@SplashActivity2
            )
            var myLocation = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (myLocation == null) {
                myLocation =
                    locationManager!!.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                pbMain.setProgress(10, true)
            } else {
                pbMain.progress = 10
            }
            if (myLocation == null) {
                Toast.makeText(this, "Waiting to get location for first time", Toast.LENGTH_SHORT)
                    .show()
            } else {
                getDataFromFireBase(myLocation)
            }
        }

    private fun shortestStoreList(location: Location?): List<Store> {
        val store3KmList: MutableList<Store> = ArrayList()
        var store: Store
        if (location != null) {
            for (i in storeList!!.indices) {
                val distance = distFrom(
                    location.latitude.toFloat(),
                    location.longitude.toFloat(),
                    storeList!![i].lat.toFloat(),
                    storeList!![i].lon.toFloat()
                )
                if (distance <= 15.1) {
                    store = Store()
                    store.id = storeList!![i].id
                    store.lat = storeList!![i].lat
                    store.lon = storeList!![i].lon
                    store.url = storeList!![i].url
                    store.name = storeList!![i].name
                    store.gmail = storeList!![i].gmail
                    store.branchCode = storeList!![i].branchCode
                    store.distance = distance
                    store.notificationTopic = storeList!![i].notificationTopic
                    store3KmList.add(store)
                }
            }
        }
        return store3KmList
    }

    private fun distFrom(lat1: Float, lng1: Float, lat2: Float, lng2: Float): Float {
        val earthRadius = 6371000.0 //meters
        val dLat = Math.toRadians((lat2 - lat1).toDouble())
        val dLng = Math.toRadians((lng2 - lng1).toDouble())
        val a =
            sin(dLat / 2) * sin(dLat / 2) + cos(Math.toRadians(lat1.toDouble())) * cos(
                Math.toRadians(lat2.toDouble())
            ) * sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val dist = (earthRadius * c).toFloat()
        return dist / 1000
    }

    private fun fetchUrlAndSubscribeTopic(storeList: List<Store>) {
        Log.d(ContentValues.TAG, "size of store: " + storeList.size)
        if (storeList.isNotEmpty()) {
            if (storeList.size <= 1) {
                subscribeToTopic(
                    storeList[0].notificationTopic,
                    storeList[0].url,
                    storeList[0].name,
                    storeList[0].gmail,
                    storeList[0].branchCode
                )
            } else {
                var minValue = storeList[0].distance
                for (i in 1 until storeList.size) {
                    minValue = java.lang.Float.min(minValue, storeList[i].distance)
                }
                for (i in storeList.indices) {
                    if (minValue == storeList[i].distance) {

                        subscribeToTopic(
                            storeList[i].notificationTopic,
                            storeList[i].url,
                            storeList[i].name,
                            storeList[i].gmail,
                            storeList[i].branchCode
                        )
                        break
                    }
                }
            }
        } else {
           // real web-view
            //code must not come here
            startWebView(Constant.BRANCH_NAME, branchCode = "")
        }
    }

    override fun onPause() {
        super.onPause()
        if (locationManager != null) locationManager!!.removeUpdates(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (locationManager != null) locationManager!!.removeUpdates(this)
    }


    companion object {
        const val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }

    override fun onLocationChanged(p0: Location) {
        getDataFromFireBase(p0)
        if (locationManager != null) locationManager!!.removeUpdates(this)
    }
}