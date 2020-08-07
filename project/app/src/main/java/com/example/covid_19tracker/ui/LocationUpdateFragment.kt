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
class LocationUpdateFragment : Fragment() {

    private var activityListener: Callbacks? = null

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

    override fun onDetach() {
        super.onDetach()

        activityListener = null
    }

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