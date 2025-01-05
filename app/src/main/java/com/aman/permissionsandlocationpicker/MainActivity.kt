package com.aman.permissionsandlocationpicker

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aman.permissionsandlocationpicker.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private val TAG = "MainActivity"
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val mFusedLocationClient: FusedLocationProviderClient by lazy{
        LocationServices.getFusedLocationProviderClient(this)
    }

    var map : GoogleMap ?= null
    var userLocation = LatLng(0.0, 0.0)
    var markerOptions = MarkerOptions()
    var mCenterMarker: Marker? = null

    private val locationPermission = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION)


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show()
            getLastLocation()
        } else {
            Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
            openAppSettings()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if(!hasPermissions()){
            requestPermissionsWithRationale()
        } else {
            getLastLocation()
        }

        // Get a handle to the fragment and register the callback.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

    }

    private fun hasPermissions(): Boolean {
        return locationPermission.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissionsWithRationale() {
        val shouldShowRationale = locationPermission.any { permission ->
            ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
        }

        if (shouldShowRationale) {
            Toast.makeText(
                this,
                "Permissions are required for the app to function properly",
                Toast.LENGTH_LONG
            ).show()
            openAppSettings()
        } else {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        requestPermissionLauncher.launch(
            locationPermission
        )
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = android.net.Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
        intent.data = uri
        startActivity(intent)
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        Log.e(TAG, "getLastLocation:  ${isLocationEnabled()} ")
        if (isLocationEnabled()) {
            Log.e(TAG, "getLastLocation: isLocationEnabled ${isLocationEnabled()}")

            // getting last
            // location from
            // FusedLocationClient
            // object

            mFusedLocationClient.lastLocation.addOnCompleteListener { task ->
                val location: Location = task.result
                if (location == null) {
                    requestNewLocationData()
                } else {
                    userLocation = LatLng(location.latitude, location.longitude)
                    updateMarker()
                }
            }
        } else {
            Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG)
                .show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest.Builder(10000)
            .build()

        mFusedLocationClient?.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            userLocation =LatLng(locationResult?.lastLocation?.latitude?:0.0, locationResult.lastLocation?.longitude?:0.0)
            updateMarker()
        }
    }

    private fun updateMarker() {
        mCenterMarker?.position = userLocation
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        mCenterMarker = map.addMarker(
            MarkerOptions()
                .position(userLocation)
        )

        map.setOnCameraIdleListener{
            userLocation = map.cameraPosition.target
            updateMarker()
        }
    }


}