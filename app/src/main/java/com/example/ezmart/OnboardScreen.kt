package com.example.ezmart

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button

class OnboardScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if Onboarding has been seen
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val isFirstTime = sharedPreferences.getBoolean("is_first_time", true)

        if (!isFirstTime) {
            // If the user has already seen onboarding, skip it
            navigateToNextScreen()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.onboard_activity)

        setupButtons()
    }

    // Navigate based on login state
    private fun navigateToNextScreen() {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        val nextActivity = if (isLoggedIn) MainActivity::class.java else Login::class.java
        startActivity(Intent(this, nextActivity))
        finish()
    }

    // Buttons initialization
    private fun setupButtons() {
        val loginButton: Button = findViewById(R.id.loginBtn_os)
        val signupButton: Button = findViewById(R.id.registerBtn_os)

        // Set click listeners
        loginButton.setOnClickListener {
            markOnboardingSeen() // Save onboarding state
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        signupButton.setOnClickListener {
            markOnboardingSeen() // Save onboarding state
            startActivity(Intent(this, Register::class.java))
            finish()
        }
    }

    // Save onboarding seen state
    private fun markOnboardingSeen() {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_first_time", false)
        editor.apply()
    }
}