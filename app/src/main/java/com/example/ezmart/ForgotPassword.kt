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

class ForgotPassword : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.forgotpass_activity)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.forgotpass_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val emailField = findViewById<TextInputEditText>(R.id.emailEt_forgotpass)
        val submitButton = findViewById<Button>(R.id.submitBtn_forgotpass)
        val loginTextview = findViewById<TextView>(R.id.loginTv_forgotpass)

        //Spannable text for loginTextView
        val registerSpannable = SpannableString("Go back to Log In")
        registerSpannable.setSpan(
            ForegroundColorSpan(Color.BLUE),
            registerSpannable.indexOf("Log In"),
            registerSpannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        loginTextview.text = registerSpannable

        loginTextview.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        // Set submit button click listener
        submitButton.setOnClickListener {
            val email = emailField.text.toString().trim()

            if (validateEmail(email)) {
                // Proceed with login
                Toast.makeText(this, "Submit Successful!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
            }
        }
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
    // Function to show a toast message
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}