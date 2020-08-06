package com.example.covid_19tracker.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.covid_19tracker.R
import com.example.covid_19tracker.common.SharedPreferenceKeys
import com.example.covid_19tracker.common.SharedPreferencesSettings


class LocationActivity : AppCompatActivity(),
    PermissionRequestFragment.Callbacks,
    LocationUpdateFragment.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        //If the user is not logged in, start login activity
        if(SharedPreferencesSettings.loadBoolean(this,SharedPreferenceKeys.LOGGED_IN_PREF) == false) kotlin.run{
            val intent:Intent = Intent(this,
                SignInActivity::class.java)
            startActivity(intent)
        }

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_location)

        if (currentFragment == null) {

            val fragment = LocationUpdateFragment.newInstance()

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_location, fragment)
                .commit()
        }
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
}
