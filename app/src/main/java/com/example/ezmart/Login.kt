package com.example.ezmart

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ezmart.api.ApiClient
import com.example.ezmart.models.LoginRequest
import com.example.ezmart.models.LoginResponse
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is already logged in
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        if (isLoggedIn) {
            navigateToMain()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.login_activity)

    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI elements
        val emailField = findViewById<TextInputEditText>(R.id.emailEt_login)
        val passwordField = findViewById<TextInputEditText>(R.id.passwordEt_login)
        val loginButton = findViewById<Button>(R.id.loginBtn)
        val forgotPasswordButton = findViewById<Button>(R.id.forgotpassbtn)
        val registerTextView = findViewById<TextView>(R.id.registerTv_login)

        // Set Spannable text for Register textView
        val registerSpannable = SpannableString("Don't have an account? Register")
        registerSpannable.setSpan(
            ForegroundColorSpan(Color.BLUE),
            registerSpannable.indexOf("Register"),
            registerSpannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        registerTextView.text = registerSpannable

        // Set login button click listener
        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (!validateEmail(email) || !validatePassword(password)) return@setOnClickListener

            val loginRequest = LoginRequest(email, password)

            // API Call
            ApiClient.instance.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    val loginResponse = response.body()
                    if (response.isSuccessful && loginResponse?.success == true) {
                        saveLoginState(true)
                        showToast("Login Successful!")
                        navigateToMain()
                    } else {
                        showToast(loginResponse?.message ?: "Invalid email or password")
                    }
                }


                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    showToast("Network error: ${t.message}")
                }
            })
        }

        // Set navigation buttons
        forgotPasswordButton.setOnClickListener {
            startActivity(Intent(this, ForgotPassword::class.java))
        }

        registerTextView.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }
    }

    // Function to validate email
    private fun validateEmail(email: String): Boolean {
        return when {
            email.isEmpty() -> {
                showToast("Email field cannot be empty.")
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showToast("Please enter a valid email address.")
                false
            }
            else -> true
        }
    }

    // Function to validate password
    private fun validatePassword(password: String): Boolean {
        return when {
            password.isEmpty() -> {
                showToast("Password field cannot be empty.")
                false
            }
            password.length !in 8..15 -> {
                showToast("Password must be between 8 and 15 characters.")
                false
            }
            else -> true
        }
    }

    // Function to show toast messages
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Navigate to MainActivity
    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    // Save login state
    private fun saveLoginState(isLoggedIn: Boolean) {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", isLoggedIn)
        editor.apply()
    }
}
