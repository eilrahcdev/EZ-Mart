package com.example.ezmart

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.splash_activity)

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splash_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Delay for 1.5 seconds before checking login state and navigating
        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

            if (isLoggedIn) {
                // Navigate to MainActivity if logged in
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Navigate to OnboardScreen if not logged in
                startActivity(Intent(this, OnboardScreen::class.java))
            }
            finish() // Close the splash screen
        }, 1500)
    }
}
