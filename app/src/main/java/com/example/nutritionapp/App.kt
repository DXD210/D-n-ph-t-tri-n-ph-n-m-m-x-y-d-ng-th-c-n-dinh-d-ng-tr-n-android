package com.example.nutritionapp

import android.app.Application
import com.example.nutritionapp.database.AppDatabase
import com.example.nutritionapp.utils.PreferenceManager

class App : Application() {
    // Lazy initialization of database
    val database by lazy { AppDatabase.getDatabase(this) }

    // Lazy initialization of preference manager
    val preferenceManager by lazy { PreferenceManager(this) }

    companion object {
        private lateinit var instance: App

        fun getInstance(): App {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}