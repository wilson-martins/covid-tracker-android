package com.example.covid_19tracker.data

import android.content.Context
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import java.util.concurrent.ExecutorService

private const val TAG = "LocationRepository"

class LocationRepository private constructor(
    private val myLocationManager: MyLocationManager
) {

    val receivingLocationUpdates: LiveData<Boolean> = myLocationManager.receivingLocationUpdates

    @MainThread
    fun startLocationUpdates() = myLocationManager.startLocationUpdates()

    @MainThread
    fun stopLocationUpdates() = myLocationManager.stopLocationUpdates()

    companion object {
        @Volatile
        private var INSTANCE: LocationRepository? = null

        fun getInstance(context: Context, executor: ExecutorService): LocationRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocationRepository(
                    MyLocationManager.getInstance(context)
                )
                    .also { INSTANCE = it }
            }
        }
    }
}