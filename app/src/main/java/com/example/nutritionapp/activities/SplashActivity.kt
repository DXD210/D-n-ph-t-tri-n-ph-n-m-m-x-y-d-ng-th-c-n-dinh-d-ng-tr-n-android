package com.example.nutritionapp.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.nutritionapp.App
import com.example.nutritionapp.R

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DISPLAY_LENGTH = 2000L // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Use a handler to delay the start of the next activity
        Handler(Looper.getMainLooper()).postDelayed({
            // Check if user is logged in
            if (App.getInstance().preferenceManager.isLoggedIn()) {
                // User is logged in, navigate to MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // User is not logged in, navigate to AuthActivity
                startActivity(Intent(this, LoginActivity::class.java))
            }

            // Close this activity
            finish()
        }, SPLASH_DISPLAY_LENGTH)
    }
}