package com.example.covid_19tracker.common

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesManager {
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context){
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE)
        }
    }

    fun setString(key: String, value: String){
        val editor :SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(key,value)
        editor.apply()
    }

    fun setBoolean(key: String, value: Boolean){
        val editor :SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean(key,value)
        editor.apply()
    }

    fun setLong(key: String, value: Long){
        val editor :SharedPreferences.Editor = sharedPreferences.edit()
        editor.putLong(key,value)
        editor.apply()
    }

    fun setFloat(key: String, value: Float){
        val editor :SharedPreferences.Editor = sharedPreferences.edit()
        editor.putFloat(key,value)
        editor.apply()
    }

    fun loadString(key: String): String? {
        return sharedPreferences.getString(key, null);
    }

    fun loadBoolean(key: String): Boolean? {
        return sharedPreferences.getBoolean(key, false);
    }

    fun loadLong(key: String): Long? {
        return sharedPreferences.getLong(key, 0);
    }

    fun loadFloat(key: String): Float? {
        return sharedPreferences.getFloat(key, 0.0F);
    }

    fun firstLogin(): Boolean?{
        return sharedPreferences.getBoolean(SharedPreferenceKeys.FIRST_LOGIN, true)
    }

}

