package com.example.covid_19tracker.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.covid_19tracker.R


private const val TAG = "SettingsActivity"

class SettingsActivity : AppCompatActivity(),
    LocationUpdateFragment.Callbacks,
    PermissionRequestFragment.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_settings)

        if (currentFragment == null) {

            val fragment = LocationUpdateFragment.newInstance()

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_settings, fragment)
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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
            .replace(R.id.fragment_settings, fragment)
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
            .replace(R.id.fragment_settings, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun displayLocationUI() {
        Log.d(TAG, "Getting locations")

        val fragment = LocationUpdateFragment.newInstance()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_settings, fragment)
            .commit()
    }
}
