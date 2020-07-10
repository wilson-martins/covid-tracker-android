package com.example.covid_19tracker.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.covid_19tracker.R

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
    PermissionRequestFragment.Callbacks,
    LocationUpdateFragment.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener {
            intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null) {

            val fragment = LocationUpdateFragment.newInstance()

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    public fun launchSecondActivity(view: View){
        intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    // Triggered from the permission Fragment that it's the app has permissions to display the
    // location fragment.
    override fun displayLocationUI() {

        val fragment = LocationUpdateFragment.newInstance()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
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
            .replace(R.id.fragment_container, fragment)
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
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

}
