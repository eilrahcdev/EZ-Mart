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

        // Delay for 1.5 seconds before navigating
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, 1500)
    }

    private fun navigateToNextScreen() {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        val isFirstTime = sharedPreferences.getBoolean("is_first_time", true)

        val nextActivity = when {
            isLoggedIn -> MainActivity::class.java
            isFirstTime -> OnboardScreen::class.java
            else -> Register::class.java
        }

        startActivity(Intent(this, nextActivity))
        finish()
    }
}

