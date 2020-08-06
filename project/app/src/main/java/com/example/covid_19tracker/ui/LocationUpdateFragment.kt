package com.example.covid_19tracker.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.covid_19tracker.R
import com.example.covid_19tracker.common.SharedPreferenceKeys
import com.example.covid_19tracker.common.SharedPreferencesSettings
import com.example.covid_19tracker.utils.LocationUpdateViewModel
import com.example.covid_19tracker.utils.hasPermission
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_location_update.view.*


private const val TAG = "LocationUpdateFragment"

/**
 * Displays location information via PendingIntent after permissions are approved.
 *
 * Will suggest "enhanced feature" to enable background location requests if not approved.
 */
class LocationUpdateFragment : Fragment(), OnMapReadyCallback {

    private var activityListener: Callbacks? = null

    private var mapView: MapView? = null

    private val locationUpdateViewModel by lazy {
        ViewModelProviders.of(this).get(LocationUpdateViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is Callbacks) {
            activityListener = context

            // If fine location permission isn't approved, instructs the parent Activity to replace
            // this fragment with the permission request fragment.
            if (!context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                activityListener?.requestFineLocationPermission()
            }
        } else {
            throw RuntimeException("$context must implement LocationUpdateFragment.Callbacks")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        binding = FragmentLocationUpdateBinding.inflate(inflater, container, false)
        val v = inflater.inflate(R.layout.fragment_location_update, container, false)

        v.enableBackgroundLocationButton.setOnClickListener {
            activityListener?.requestBackgroundLocationPermission()
        }

        // Gets the MapView from the XML layout and creates it
        mapView = v.findViewById(R.id.locationMapFragment) as MapView
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationUpdateViewModel.receivingLocationUpdates.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { receivingLocation ->
                updateStartOrStopButtonState(view, receivingLocation)
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onResume() {
        super.onResume()
        updateBackgroundButtonState()
        mapView?.onResume()
        mapView?.getMapAsync(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapView?.onCreate(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDetach() {
        super.onDetach()

        activityListener = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val latLng = LatLng(
            SharedPreferencesSettings.loadString(
                context,
                SharedPreferenceKeys.LATITUDE
            )?.toDouble() ?: 0.0,
            SharedPreferencesSettings.loadString(
                context,
                SharedPreferenceKeys.LONGITUDE
            )?.toDouble() ?: 0.0
        )
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
//        mapView?.onResume()
    }
//        locationUpdateViewModel.locationListLiveData.observe(
//            viewLifecycleOwner,
//            androidx.lifecycle.Observer { locations ->
//                locations?.let {
//                    Log.d(TAG, "Got ${locations.size} locations")
//
//                    if (locations.isEmpty()) {
//                        googleMap?.apply {
//                            val loc = LatLng(location.latitude, location.longitude)
//                            addMarker(
//                                MarkerOptions()
//                                    .position(loc)
//                                    .title("Visited place")
//                            )
//                        }
//                    } else {
//                        for (location in locations) {
//                            googleMap?.apply {
//                                val loc = LatLng(location.latitude, location.longitude)
//                                addMarker(
//                                    MarkerOptions()
//                                        .position(loc)
//                                        .title("Visited place")
//                                )
//                            }
//                        }
//
//                    }
//                }
//            }
//        )
//}

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showBackgroundButton(): Boolean {
        return !requireContext().hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun updateBackgroundButtonState() {

        if (showBackgroundButton()) {
            view?.enableBackgroundLocationButton?.visibility = View.VISIBLE
        } else {
            view?.enableBackgroundLocationButton?.visibility = View.GONE
        }
    }

    private fun updateStartOrStopButtonState(view: View, receivingLocation: Boolean) {
        if (receivingLocation) {
            view.startOrStopLocationUpdatesButton.apply {
                text = getString(R.string.stop_receiving_location)
                setOnClickListener {
                    locationUpdateViewModel.stopLocationUpdates()
                }
            }
        } else {
            view.startOrStopLocationUpdatesButton.apply {
                text = getString(R.string.start_receiving_location)
                setOnClickListener {
                    locationUpdateViewModel.startLocationUpdates()
                }
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface Callbacks {
        fun requestFineLocationPermission()
        fun requestBackgroundLocationPermission()
    }

    companion object {
        fun newInstance() =
            LocationUpdateFragment()
    }
}