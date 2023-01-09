package com.aashutosh.desimall_pro.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment(private val stringExtra: String) : Fragment(), OnMapReadyCallback {
    private val latLon: String = stringExtra
    lateinit var binding: FragmentMapBinding
    private var mMap: GoogleMap? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return binding.root
    }


    override fun onMapReady(googleMap: GoogleMap) {

        val value = latLon.trim().split(",")
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val currentLatLon = LatLng(value[0].toDouble(), value[1].toDouble())
        mMap!!.addMarker(MarkerOptions().position(currentLatLon))
        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLon, 20f))


    }

}