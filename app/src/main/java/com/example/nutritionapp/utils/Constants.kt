package com.example.nutritionapp.utils

object Constants {
    // Shared Preferences
    const val PREF_NAME = "nutrition_app_prefs"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_EMAIL = "user_email"
    const val KEY_USER_NAME = "user_name"
    const val KEY_IS_LOGGED_IN = "is_logged_in"

    // Intent extras
    const val EXTRA_MEAL_ID = "meal_id"
    const val EXTRA_PLAN_ID = "plan_id"
    const val EXTRA_NOTE_ID = "note_id"

    // Request codes
    const val REQUEST_IMPORT_DATABASE = 100
    const val REQUEST_EXPORT_DATABASE = 101

    // File names
    const val EXPORT_DB_FILENAME = "nutrition_app_backup.db"
}