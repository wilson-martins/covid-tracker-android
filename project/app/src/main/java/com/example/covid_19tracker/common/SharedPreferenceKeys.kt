package com.example.covid_19tracker.common

class SharedPreferenceKeys {
    companion object {
        // Values for Shared Prefrences
        const val LOGGED_IN_PREF: String = "logged_in_status"
        const val FIRST_LOGIN: String = "first_login"
        const val PERSON_ID: String = "person_id"

        // Keys from Google's user account
        const val GOOGLE_ID = "google_id"
        const val GOOGLE_FIRST_NAME = "google_first_name"
        const val GOOGLE_LAST_NAME = "google_last_name"
        const val GOOGLE_EMAIL = "google_email"
        const val GOOGLE_PROFILE_PICTURE_URL = "google_profile_picture_url"
        const val GOOGLE_ID_TOKEN = "google_id_token"

        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
    }
}