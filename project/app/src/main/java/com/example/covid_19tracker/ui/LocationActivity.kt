package com.example.covid_19tracker.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.example.covid_19tracker.R
import com.example.covid_19tracker.common.SharedPreferenceKeys
import com.example.covid_19tracker.common.SharedPreferencesManager
import com.example.covid_19tracker.service.LocationService
import com.example.covid_19tracker.utils.LocationUpdateViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val TAG = "LocationActivity"

class LocationActivity : AppCompatActivity(),
    PermissionRequestFragment.Callbacks,
    LocationUpdateFragment.Callbacks,
    OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationService: LocationService
    private var locationPermissionGranted = false
    private var lastKnownLocation: Location? = null

    val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2000

    private val locationUpdateViewModel by lazy {
        ViewModelProviders.of(this).get(LocationUpdateViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationService = LocationService.create()
        locationUpdateViewModel.startLocationUpdates()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.locationMapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

//        //If the user is not logged in, start login activity
//        if (SharedPreferencesSettings.loadBoolean(
//                this,
//                SharedPreferenceKeys.LOGGED_IN_PREF
//            ) == false
//        ) kotlin.run {
//            val intent: Intent = Intent(
//                this,
//                SignInActivity::class.java
//            )
//            startActivity(intent)
//        }
//
//
//        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_location)
//
//        if (currentFragment == null) {
//
//            val fragment = LocationUpdateFragment.newInstance()
//
//            supportFragmentManager
//                .beginTransaction()
//                .replace(R.id.fragment_location, fragment)
//                .commit()
//        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    // Triggered from the permission Fragment that it's the app has permissions to display the
    // location fragment.
    override fun displayLocationUI() {

        val fragment = LocationUpdateFragment.newInstance()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_location, fragment)
            .commit()
    }

    // Triggers a splash screen (fragment) to help users decide if they want to approve the missing
    // fine location permission.
    override fun requestFineLocationPermission() {
        val fragment =
            PermissionRequestFragment.newInstance(
                PermissionRequestType.FINE_LOCATION
            )

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_location, fragment)
            .addToBackStack(null)
            .commit()
    }

    // Triggers a splash screen (fragment) to help users decide if they want to approve the missing
    // background location permission.
    override fun requestBackgroundLocationPermission() {
        val fragment =
            PermissionRequestFragment.newInstance(
                PermissionRequestType.BACKGROUND_LOCATION
            )

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_location, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        addMarkers();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }


    private fun updateLocationUI() {
        try {
            if (locationPermissionGranted) {
                mMap.isMyLocationEnabled = true
                mMap.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                mMap.isMyLocationEnabled = false
                mMap.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = mFusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), 12.0f
                                )
                            )
                        }
                    } else {
                        mMap.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(LatLng(0.0, 0.0), 8.0f)
                        )
                        mMap.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun addMarkers() {
        val personId: Long = SharedPreferencesManager.loadLong(SharedPreferenceKeys.PERSON_ID)
            ?: return
        locationService.getByPersonId(personId)
            .enqueue(object : Callback<List<com.example.covid_19tracker.model.Location>> {
                override fun onFailure(
                    call: Call<List<com.example.covid_19tracker.model.Location>>?,
                    t: Throwable?
                ) {
                    Log.e(TAG,"Error on getByPersonId request")
                }

                override fun onResponse(
                    call: Call<List<com.example.covid_19tracker.model.Location>>?,
                    response: Response<List<com.example.covid_19tracker.model.Location>>?
                ) {
                    response?.body()?.forEach { l ->
                        mMap.addMarker(MarkerOptions().position(LatLng(l.latitude, l.longitude)))
                    }
                }
            })
    }

}
