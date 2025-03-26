package com.example.ezmart

import android.annotation.SuppressLint
import android.app.ProgressDialog
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
import com.example.ezmart.api.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class ForgotPassword : AppCompatActivity() {

    private lateinit var progressDialog: ProgressDialog

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

        // Initialize progress dialog
        progressDialog = ProgressDialog(this).apply {
            setMessage("Sending reset link...")
            setCancelable(false)
        }

        // Spannable text for loginTextView
        val registerSpannable = SpannableString("Go back to Log In")
        registerSpannable.setSpan(
            ForegroundColorSpan(Color.BLUE),
            registerSpannable.indexOf("Log In"),
            registerSpannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        loginTextview.text = registerSpannable

        // Navigate back to Login activity
        loginTextview.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        // Set submit button click listener
        submitButton.setOnClickListener {
            val email = emailField.text.toString().trim()

            if (validateEmail(email)) {
                // Proceed with API call to request password reset
                requestPasswordReset(email)
            }
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

    // Function to show a toast message
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Function to request password reset via Retrofit API
    private fun requestPasswordReset(email: String) {
        // Show progress dialog
        progressDialog.show()

        // Create request body
        val request = mapOf("email" to email)

        // Make Retrofit API call
        RetrofitClient.instance.requestPasswordReset(request).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                // Hide progress dialog
                progressDialog.dismiss()

                if (response.isSuccessful) {
                    // Handle success
                    showToast("Password reset link sent to your email.")
                    val intent = Intent(this@ForgotPassword, Login::class.java)
                    startActivity(intent)
                    finish() // Close the current activity
                } else {
                    // Handle error response
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    showToast("Failed to send reset link: $errorBody")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Hide progress dialog
                progressDialog.dismiss()

                // Handle network failure
                showToast("Network error. Please check your connection.")
            }
        })
    }
}