package com.example.covid_19tracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.covid_19tracker.R
import com.example.covid_19tracker.common.SharedPreferenceKeys
import com.example.covid_19tracker.common.SharedPreferencesSettings
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //If the user is not logged in, start login activity
        if(SharedPreferencesSettings.loadBoolean(this,SharedPreferenceKeys.LOGGED_IN_PREF) == false) kotlin.run{
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
    fun startUserProfileActivity(view: View){
        intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

}
