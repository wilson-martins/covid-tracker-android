package com.example.covid_19tracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.covid_19tracker.R
import com.example.covid_19tracker.common.SharedPreferenceKeys
import com.example.covid_19tracker.common.SharedPreferencesManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BasicActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //If the user is not logged in, start login activity
        if(SharedPreferencesManager.loadBoolean(SharedPreferenceKeys.LOGGED_IN_PREF) == false) kotlin.run{
            val intent:Intent = Intent(this,
                SignInActivity::class.java)
            startActivity(intent)
        }

    }

    fun editHealthInformation(view: View){
        intent = Intent(this, HealthStateActivity::class.java)
        startActivity(intent)
    }

    fun openLocations(view: View){
        intent = Intent(this, LocationActivity::class.java)
        startActivity(intent)
    }

    fun startUserProfileActivity(view: View){
        // Go to sign up activity
        intent = Intent(this, EditUserInformationActivity::class.java)
        startActivity(intent)
    }

}
