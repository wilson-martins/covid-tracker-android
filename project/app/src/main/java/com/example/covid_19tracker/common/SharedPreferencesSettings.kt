package com.example.covid_19tracker.common

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager.getDefaultSharedPreferences


class SharedPreferencesSettings{
    companion object{

        fun getPreferences(context: Context?): SharedPreferences {
            return getDefaultSharedPreferences(context)
        }

        /**
         * Save a string shared preference
         * @param context
         * @param key
         * @param value
         */
        fun setString(context: Context?, key : String, value: String){
            val editor :SharedPreferences.Editor = getPreferences(context).edit()
            editor.putString(key,value)
            editor.apply()
        }

        fun setBoolean(context: Context?, key : String, value: Boolean){
            val editor :SharedPreferences.Editor = getPreferences(context).edit()
            editor.putBoolean(key,value)
            editor.apply()
        }

        fun setLong(context: Context?, key : String, value: Long){
            val editor :SharedPreferences.Editor = getPreferences(context).edit()
            editor.putLong(key,value)
            editor.apply()
        }

        /**
         * Load a shared preference
         *  @param context
         *  @param key
         *  @return the value associated with the given key, null if the key doesn't exist
         */

        fun loadString(context: Context?, key: String): String? {
            return getPreferences(context).getString(key, null);
        }

        fun loadBoolean(context: Context?, key: String): Boolean? {
            return getPreferences(context).getBoolean(key, false);
        }

        fun loadLong(context: Context?, key: String): Long? {
            return getPreferences(context).getLong(key, 0);
        }

        fun firstLogin(context: Context?): Boolean?{
            return getPreferences(context).getBoolean(SharedPreferenceKeys.FIRST_LOGIN, true)
        }

    }
}