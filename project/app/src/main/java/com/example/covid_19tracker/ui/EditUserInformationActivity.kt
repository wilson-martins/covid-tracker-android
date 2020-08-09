package com.example.covid_19tracker.ui

import android.widget.Toast

class EditUserInformationActivity : SignUpActivity() {
    override fun signUp() {
        if (validForm) {
            saveUserDataOnSharedPreferences()
            //TODO update remote user information using API
            Toast.makeText(this@EditUserInformationActivity, "Updated data", Toast.LENGTH_LONG).show()
        }
    }

}