package com.example.nutritionapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.nutritionapp.utils.Constants.KEY_IS_LOGGED_IN
import com.example.nutritionapp.utils.Constants.KEY_USER_EMAIL
import com.example.nutritionapp.utils.Constants.KEY_USER_ID
import com.example.nutritionapp.utils.Constants.KEY_USER_NAME
import com.example.nutritionapp.utils.Constants.PREF_NAME

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveLoginSession(userId: Long, email: String, username: String) {
        val editor = sharedPreferences.edit()
        editor.putLong(KEY_USER_ID, userId)
        editor.putString(KEY_USER_EMAIL, email)
        editor.putString(KEY_USER_NAME, username)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.apply()
    }

    fun clearLoginSession() {
        val editor = sharedPreferences.edit()
        editor.remove(KEY_USER_ID)
        editor.remove(KEY_USER_EMAIL)
        editor.remove(KEY_USER_NAME)
        editor.putBoolean(KEY_IS_LOGGED_IN, false)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUserId(): Long {
        return sharedPreferences.getLong(KEY_USER_ID, -1)
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }

    fun getUserName(): String? {
        return sharedPreferences.getString(KEY_USER_NAME, null)
    }
}