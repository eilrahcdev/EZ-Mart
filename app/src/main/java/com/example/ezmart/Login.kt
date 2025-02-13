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
import com.google.android.material.textfield.TextInputEditText

class Login : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_activity)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Find views
        val emailField = findViewById<TextInputEditText>(R.id.emailEt_login)
        val passwordField = findViewById<TextInputEditText>(R.id.passwordEt_login)
        val loginButton = findViewById<Button>(R.id.loginBtn)
        val forgotPasswordButton = findViewById<Button>(R.id.forgotpassbtn)
        val registerTextView = findViewById<TextView>(R.id.registerTv_login)

        // Set Spannable text for signupTextView
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

            if (validateEmail(email) && validatePassword(password)) {
                // Proceed with login
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
            }
        }

        // Set navigation for Sign Up and Forgot Password buttons
        setupNavigationButtons(forgotPasswordButton, loginButton, registerTextView)
    }

    // Function to validate email
    private fun validateEmail(email: String): Boolean {
        return if (email.isEmpty()) {
            showToast("Email field cannot be empty.")
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email address.")
            false
        } else {
            true
        }
    }

    // Function to validate password
    private fun validatePassword(password: String): Boolean {
        return if (password.isEmpty()) {
            showToast("Password field cannot be empty.")
            false
        } else if (password.length < 8 || password.length > 15) {
            showToast("Password must be between 8 and 15 characters.")
            false
        } else {
            true
        }
    }

    // Function to show a toast message
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Function to set navigation for Sign Up and Forgot Password buttons
    private fun setupNavigationButtons(
        forgotPasswordButton: Button,
        loginButton: Button,
        registerTextview: TextView
    ) {
        forgotPasswordButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        registerTextview.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }
}
