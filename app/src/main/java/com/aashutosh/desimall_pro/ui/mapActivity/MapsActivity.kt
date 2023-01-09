package com.aashutosh.desimall_pro.ui.mapActivity

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.databinding.ActivityMapsBinding
import com.aashutosh.desimall_pro.ui.HomeActivity
import com.aashutosh.desimall_pro.ui.bottomSheetFragments.AddressBottomSheet
import com.aashutosh.desimall_pro.utils.Constant
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {
    private lateinit var sharedPrefHelper: SharedPrefHelper
    private lateinit var binding: ActivityMapsBinding
    private var mMap: GoogleMap? = null
    var alertDialog: AlertDialog? = null
    var locationManager: LocationManager? = null
    var onStop = false
    lateinit var finalLat: String
    lateinit var finalLon: String
    private lateinit var progressDialog: AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(applicationContext)
        checkAllStuff()
        binding.btNext.setOnClickListener(View.OnClickListener {
            if (intent.getBooleanExtra(
                    Constant.VERIFY_USER_LOCATION,
                    false
                ) || intent.getBooleanExtra(
                    Constant.IS_PROFILE,
                    false
                )
            ) {
                addMapDetails()
            } else {
                this.finish()
            }
        })
        if (intent.getBooleanExtra(Constant.DETAILS, false)) {
            AddressBottomSheet().show(supportFragmentManager, "short")
            binding.btNext.visibility = View.GONE
        }

        if (intent.getBooleanExtra(Constant.VERIFY_USER_LOCATION, false) || intent.getBooleanExtra(
                Constant.IS_PROFILE,
                false
            )
        ) {
            binding.tvTop.text = "Pin your delivery location"
        }


        binding.tvSkip.setOnClickListener(View.OnClickListener {
            val i = Intent(this@MapsActivity, HomeActivity::class.java)
            sharedPrefHelper[Constant.USER_SKIPPED] = true
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        })
    }

    private fun validateLatLon(): Boolean {
        return if (finalLat.isNullOrEmpty()) {
            Toast.makeText(this@MapsActivity, "Please wait a while", Toast.LENGTH_SHORT)
                .show()
            false

        } else {
            true
        }
    }


    private fun initProgressDialog(): AlertDialog {
        progressDialog = Constant.setProgressDialog(this, "Validating your Location")
        progressDialog.setCancelable(false) // blocks UI interaction
        return progressDialog
    }

    private fun addMapDetails() {
        if (validateLatLon()) {
            initProgressDialog().show()
            val db = Firebase.firestore
            val createUser = hashMapOf(
                "location" to "$finalLat,$finalLon",
            )
            db.collection("user").document(sharedPrefHelper[Constant.PHONE_NUMBER])
                .update(createUser as Map<String, Any>).addOnSuccessListener {
                    binding.btNext.visibility = View.GONE
                    sharedPrefHelper[Constant.VERIFIED_LOCATION] = true
                    sharedPrefHelper[Constant.LOCATION] = "$finalLat,$finalLon"

                    progressDialog.dismiss()
                    if (intent.getBooleanExtra(
                            Constant.IS_PROFILE,
                            false
                        )
                    ) {
                        Toast.makeText(this@MapsActivity, "Location Updated", Toast.LENGTH_SHORT)
                            .show()
                        this.finish()
                    } else {
                        Toast.makeText(this@MapsActivity, "Location Added", Toast.LENGTH_SHORT)
                            .show()
                        AddressBottomSheet().show(supportFragmentManager, "short")
                    }

                }.addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@MapsActivity,
                        "Unable to Add location. Try again later",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
        }
    }

    private fun startMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val lat: String = sharedPrefHelper[Constant.LAT]
        val lon: String = sharedPrefHelper[Constant.LON]


        finalLat = lat;
        finalLon = lon;
        // Add a marker in Sydney and move the camera
        val currentLatLon = LatLng(lat.toDouble(), lon.toDouble())

        mMap!!.addMarker(
            MarkerOptions().position(currentLatLon).title("Marker in your location").icon(
                (BitmapDescriptorFactory.fromBitmap(drawableToBitmap(getDrawable(R.drawable.my_location)!!)!!))
            )
        )

        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLon, 20f))


        mMap!!.setOnMapClickListener {
            finalLat = it.latitude.toString()
            finalLon = it.longitude.toString()

            sharedPrefHelper[Constant.LAT_LON] =
                it.latitude.toString() + "," + it.longitude.toString()
            mMap!!.clear()
            mMap!!.addMarker(MarkerOptions().position(it))
        }


    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        var bitmap: Bitmap? = null
        if (drawable is BitmapDrawable) {
            val bitmapDrawable = drawable
            if (bitmapDrawable.bitmap != null) {
                return bitmapDrawable.bitmap
            }
        }
        bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
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

    companion object {
        const val REQUEST_PERMISSIONS_REQUEST_CODE = 34
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
                        this@MapsActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSIONS_REQUEST_CODE
                    )
                }.show()
            }
        }
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
                    this@MapsActivity,
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
                this@MapsActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
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
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, this)
            var myLocation = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (myLocation == null) {
                myLocation =
                    locationManager!!.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            }

            if (myLocation == null) {
                Toast.makeText(this, "Waiting to get location for first time", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (locationManager != null) locationManager!!.removeUpdates(this)
                sharedPrefHelper[Constant.LAT] = myLocation.latitude.toString()
                sharedPrefHelper[Constant.LON] = myLocation.longitude.toString()
                sharedPrefHelper[Constant.LAT_LON] =
                    myLocation.latitude.toString() + "," + myLocation.longitude.toString()
                startMap();
                //here

            }
        }

    override fun onPause() {
        super.onPause()
        if (locationManager != null) locationManager!!.removeUpdates(this)
    }

    override fun onResume() {
        super.onResume()
        if (onStop) {
            if (alertDialog != null && alertDialog!!.isShowing) {
                alertDialog!!.dismiss()
            }

            checkAllStuff()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (locationManager != null) locationManager!!.removeUpdates(this)
    }

    private fun checkPermissions(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
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
                ) { paramDialogInterface, paramInt -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                    .setNegativeButton(
                        "Cancel"
                    ) { dialog, which ->
                        this.finish()
                    }.show()
            false
        } else {
            true
        }
    }

    override fun onLocationChanged(location: Location) {
        sharedPrefHelper[Constant.LAT] = location.latitude.toString()
        sharedPrefHelper[Constant.LON] = location.longitude.toString()
        sharedPrefHelper[Constant.LAT_LON] =
            location.latitude.toString() + "," + location.longitude.toString()
        //here
        if (locationManager != null) locationManager!!.removeUpdates(this)
        startMap();

    }
}