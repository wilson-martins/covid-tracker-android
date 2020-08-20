package com.example.covid_19tracker.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.example.covid_19tracker.R
import com.example.covid_19tracker.common.SharedPreferenceKeys
import com.example.covid_19tracker.common.SharedPreferencesManager
import com.example.covid_19tracker.data.MyLocationManager
import com.example.covid_19tracker.model.Person
import com.example.covid_19tracker.model.UpdateGoogleIdToken
import com.example.covid_19tracker.service.PersonService
import com.example.covid_19tracker.utils.LocationUpdateViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : BasicActivity() {

    private lateinit var personService: PersonService

    private val locationUpdateViewModel by lazy {
        ViewModelProviders.of(this).get(LocationUpdateViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //If the user is not logged in, start login activity
        if (SharedPreferencesManager.loadBoolean(SharedPreferenceKeys.LOGGED_IN_PREF) == false) kotlin.run {
            val intent: Intent = Intent(
                this,
                SignInActivity::class.java
            )
            startActivity(intent)
        }

        if(SharedPreferencesManager.loadBoolean(SharedPreferenceKeys.LOCATION_UPDATES_ACTIVE) == true) kotlin.run{
            locationUpdateViewModel.startLocationUpdates()
        }

        personService = PersonService.create()

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                sendRegistrationToServer(token)
            })

    }

    private fun sendRegistrationToServer(token: String?) {
        val payload = UpdateGoogleIdToken(personId = SharedPreferencesManager.loadLong(SharedPreferenceKeys.PERSON_ID), googleIdToken = token)
        personService.updateGoogleIdToken(payload).enqueue(object :
            Callback<String?> {
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Log.d(TAG, "Error updating google Id token ")
            }

            override fun onResponse(
                call: Call<String?>,
                response: Response<String?>
            ) {
                if (response.isSuccessful) {
                    Log.d(TAG, "Google Id Token Update")
                } else {
                    Log.d(TAG, "Error updating google Id token ")
                }
            }
        })
    }

    fun editHealthInformation(view: View) {
        intent = Intent(this, HealthStateActivity::class.java)
        startActivity(intent)
    }

    fun openLocations(view: View) {
        intent = Intent(this, LocationActivity::class.java)
        startActivity(intent)
    }

    fun startUserProfileActivity(view: View) {
        // Go to sign up activity
        intent = Intent(this, EditUserInformationActivity::class.java)
        startActivity(intent)
    }

    companion object {

        private const val TAG = "MainActivity"
    }

    fun startAppInformationActivity(view: View) {
        intent = Intent(this, AppInformationActivity::class.java)
        startActivity(intent)
    }

}
