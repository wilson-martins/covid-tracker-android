package com.example.covid_19tracker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.example.covid_19tracker.common.SharedPreferenceKeys
import com.example.covid_19tracker.common.SharedPreferencesManager
import com.example.covid_19tracker.model.Location
import com.example.covid_19tracker.service.LocationService
import com.google.android.gms.location.LocationResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

private const val TAG = "LUBroadcastReceiver"

class LocationUpdatesBroadcastReceiver : BroadcastReceiver() {

    private var locationService: LocationService = LocationService.create()

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive() context:$context, intent:$intent")

        if (intent.action == ACTION_PROCESS_UPDATES) {
            LocationResult.extractResult(intent)?.let { locationResult ->
                val locations = locationResult.locations.map { location ->
                    Location(
                        1L,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        lastVisit = Date()
                    )
                }
                if (locations.isNotEmpty()) {
                    locations.forEach { l ->
                        locationService.registerLocation(Location(SharedPreferencesManager.loadLong(SharedPreferenceKeys.PERSON_ID), l.latitude, l.longitude, Date())).enqueue(object :
                            Callback<Location?> {
                            override fun onFailure(call: Call<Location?>, t: Throwable) {
                                Log.d(TAG, "Error adding location. lat: ${l.latitude}, long: ${l.longitude}")
                            }

                            override fun onResponse(
                                call: Call<Location?>,
                                response: Response<Location?>
                            ) {
                                if (response.body() != null && response.body()?.personId != 0L) {
                                    Log.d(TAG, "New Location added. lat: ${l.latitude}, long: ${l.longitude}")
                                } else {
                                    Log.d(TAG, "Error adding location. lat: ${l.latitude}, long: ${l.longitude}")
                                }
                            }
                        })
                    }
                }
                SharedPreferencesManager.setString(
                    SharedPreferenceKeys.LATITUDE,
                    locationResult.lastLocation.latitude.toString()
                )
                SharedPreferencesManager.setString(
                    SharedPreferenceKeys.LONGITUDE,
                    locationResult.lastLocation.longitude.toString()
                )
            }
        }
    }

    companion object {
        const val ACTION_PROCESS_UPDATES =
            "com.example.covid_19tracker.action." +
                    "PROCESS_UPDATES"
    }
}


