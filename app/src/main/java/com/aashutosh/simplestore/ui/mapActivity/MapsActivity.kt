package com.aashutosh.simplestore.ui.mapActivity

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.aashutosh.simplestore.R
import com.aashutosh.simplestore.databinding.ActivityMapsBinding
import com.aashutosh.simplestore.splash.test.SplashActivity2
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    var alertDialog: AlertDialog? = null
    var onStop = false
    var locationManager: LocationManager? = null
    var userLocation: Location? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (alertDialog != null && alertDialog!!.isShowing) alertDialog!!.dismiss()
        checkAllStuff()
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

        // Add a marker in Sydney and move the camera
        // val sydney = LatLng(-34.0, 151.0)
        val userLatLng = LatLng(userLocation!!.latitude, userLocation!!.longitude)
        mMap.addMarker(MarkerOptions().position(userLatLng).title("Mark to your location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLatLng))
    }

    override fun onStop() {
        super.onStop()
        onStop = true
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
                    SplashActivity2.REQUEST_PERMISSIONS_REQUEST_CODE
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
                SplashActivity2.REQUEST_PERMISSIONS_REQUEST_CODE
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
                userLocation = myLocation
                if (locationManager != null) locationManager!!.removeUpdates(this)

            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i(ContentValues.TAG, "onRequestPermissionResult")
        if (requestCode == SplashActivity2.REQUEST_PERMISSIONS_REQUEST_CODE) {
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
                        SplashActivity2.REQUEST_PERMISSIONS_REQUEST_CODE
                    )
                }.show()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
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

    override fun onLocationChanged(p0: Location) {
        userLocation = p0
        if (locationManager != null) locationManager!!.removeUpdates(this)
    }
}


