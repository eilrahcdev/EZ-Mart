@file:Suppress("DEPRECATION")

package com.example.ezmart.auth

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.ezmart.Login
import com.example.ezmart.R
import com.example.ezmart.api.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var progressDialog: ProgressDialog
    private lateinit var email: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.reset_password_activity)

        email = intent.getStringExtra("email") ?: run {
            showToast("Email not found")
            finish()
            return
        }

        val otpField = findViewById<TextInputEditText>(R.id.otpEt)
        val newPasswordField = findViewById<TextInputEditText>(R.id.newPasswordEt)
        val confirmPasswordField = findViewById<TextInputEditText>(R.id.confirmPasswordEt)
        val submitButton = findViewById<Button>(R.id.submitBtn_reset)

        progressDialog = ProgressDialog(this).apply {
            setMessage("Securely updating your password...")
            setCancelable(false)
        }

        submitButton.setOnClickListener {
            val otp = otpField.text.toString().trim()
            val newPassword = newPasswordField.text.toString().trim()
            val confirmPassword = confirmPasswordField.text.toString().trim()

            if (validateInputs(otp, newPassword, confirmPassword)) {
                resetPassword(email, otp, newPassword)
            }
        }
    }

    private fun validateInputs(otp: String, newPassword: String, confirmPassword: String): Boolean {
        return when {
            otp.isEmpty() -> {
                showToast("OTP is required")
                false
            }
            otp.length != 6 -> {
                showToast("OTP must be 6 digits.")
                false
            }
            newPassword.isEmpty() -> {
                showToast("New password is required.")
                false
            }
            newPassword.length < 8 -> {
                showToast("Password must be at least 8 characters.")
                false
            }
            !newPassword.matches(".*[A-Z].*".toRegex()) -> {
                showToast("Password must contain at least one uppercase letter.")
                false
            }
            !newPassword.matches(".*[a-z].*".toRegex()) -> {
                showToast("Password must contain at least one lowercase letter.")
                false
            }
            !newPassword.matches(".*\\d.*".toRegex()) -> {
                showToast("Password must contain at least one number.")
                false
            }
            confirmPassword.isEmpty() -> {
                showToast("Please confirm your password.")
                false
            }
            newPassword != confirmPassword -> {
                showToast("Passwords do not match.")
                false
            }
            else -> true
        }
    }

    private fun resetPassword(email: String, otp: String, newPassword: String) {
        progressDialog.show()

        val resetRequest = ResetPasswordRequest(
            email = email,
            otp = otp,
            new_password = newPassword
        )

        RetrofitClient.instance.resetPassword(resetRequest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()

                try {
                    val responseBody = response.body()?.string()
                    Log.d("ResetPassword", "Server Response: $responseBody")

                    if (response.isSuccessful) {
                        val jsonResponse = JSONObject(responseBody)
                        if (jsonResponse.getBoolean("success")) {
                            showToast("Password reset successfully.")
                            navigateToLogin()
                        } else {
                            // This is the critical fix - handle unsuccessful responses
                            val errorMessage = jsonResponse.optString("error", "Password reset failed")
                            showToast(errorMessage)
                            Log.e("ResetPassword", "Server error: $errorMessage")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = try {
                            JSONObject(errorBody).getString("error")
                        } catch (e: Exception) {
                            "Invalid OTP or expired token"
                        }
                        showToast(errorMessage)
                    }
                } catch (e: Exception) {
                    showToast("Error processing response.")
                    Log.e("ResetPassword", "Response error", e)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                showToast("Network error, please try again later.")
                Log.e("ResetPassword", "Network failure", t)
            }
        })
    }

    private fun navigateToLogin() {
        val intent = Intent(this, Login::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finishAffinity()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}