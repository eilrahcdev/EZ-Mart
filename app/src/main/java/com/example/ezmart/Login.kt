package com.example.ezmart

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ezmart.api.RetrofitClient
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
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val loggedInUser = sharedPreferences.getString("loggedInUser", null)

        if (loggedInUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
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

        // Set Spannable text for Register TextView
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
            RetrofitClient.instance.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    val loginResponse = response.body()
                    if (response.isSuccessful && loginResponse?.success == true) {
                        showToast("Login Successful!")
                        Log.d("LoginActivity", "Login successful. Navigating to MainActivity.")

                        // Save user login state & retrieve user details
                        saveLoginState(email)

                        // Navigate to MainActivity after successful login
                        startActivity(Intent(this@Login, MainActivity::class.java))
                        finish()
                    } else {
                        showToast(loginResponse?.message ?: "Invalid email or password")
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    showToast("Network error")
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

    // Save login state & store logged-in user
    private fun saveLoginState(email: String) {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("loggedInUser", email)
        editor.apply()
    }

    // Retrieve logged-in user details
    private fun getUserData(email: String): Map<String, String> {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        return mapOf(
            "firstName" to (sharedPreferences.getString("${email}_firstName", "") ?: ""),
            "lastName" to (sharedPreferences.getString("${email}_lastName", "") ?: ""),
            "birthdate" to (sharedPreferences.getString("${email}_birthdate", "") ?: ""),
            "contact" to (sharedPreferences.getString("${email}_contact", "") ?: ""),
            "address" to (sharedPreferences.getString("${email}_address", "") ?: ""),
            "gender" to (sharedPreferences.getString("${email}_gender", "") ?: "")
        )
    }
}