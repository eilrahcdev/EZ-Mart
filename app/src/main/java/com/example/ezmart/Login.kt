package com.example.ezmart

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
import com.example.ezmart.models.User
import com.example.ezmart.utils.UserSession
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {

    private lateinit var userSession: UserSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("LoginActivity", "onCreate: Initializing Login Activity.")

        userSession = UserSession(this)

        val loggedInUser = userSession.getUser()?.id?.toString()

        Log.d("LoginActivity", "Checking user session on app start: User ID -> $loggedInUser")

        if (loggedInUser != null) {
            Log.d("LoginActivity", "User already logged in. Redirecting to MainActivity.")
            navigateToMainActivity()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.login_activity)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val emailField = findViewById<TextInputEditText>(R.id.emailEt_login)
        val passwordField = findViewById<TextInputEditText>(R.id.passwordEt_login)
        val loginButton = findViewById<Button>(R.id.loginBtn)
        val forgotPasswordButton = findViewById<Button>(R.id.forgotpassbtn)
        val registerTextView = findViewById<TextView>(R.id.registerTv_login)

        val registerSpannable = SpannableString("Don't have an account? Register")
        registerSpannable.setSpan(
            ForegroundColorSpan(Color.BLUE),
            registerSpannable.indexOf("Register"),
            registerSpannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        registerTextView.text = registerSpannable

        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            Log.d("LoginActivity", "Login button clicked. Email: $email, Password: ${password.length} characters.")

            if (!validateEmail(email) || !validatePassword(password)) {
                Log.d("LoginActivity", "Validation failed. Email: $email, Password length: ${password.length}")
                return@setOnClickListener
            }

            val loginRequest = LoginRequest(email, password)
            Log.d("LoginActivity", "Sending login request: $loginRequest")

            RetrofitClient.instance.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    val loginResponse = response.body()
                    Log.d("LoginActivity", "API Response: ${response.raw()}")
                    Log.d("LoginActivity", "Parsed Response Body: $loginResponse")

                    if (response.isSuccessful && loginResponse?.success == true) {
                        showToast("Login Successful! ✅")
                        Log.d("LoginActivity", "Login successful. Saving user session.")

                        saveLoginState(loginResponse.user)
                        navigateToMainActivity()
                    } else {
                        showToast(loginResponse?.message ?: "Invalid email or password ❌")
                        Log.e("LoginActivity", "Login failed. Response Code: ${response.code()}, Message: ${loginResponse?.message}")
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    showToast("Network error: ${t.message} ❌")
                    Log.e("LoginActivity", "Login API call failed: ${t.message}", t)
                }
            })
        }

        forgotPasswordButton.setOnClickListener {
            Log.d("LoginActivity", "Navigating to Forgot Password screen.")
            startActivity(Intent(this, ForgotPassword::class.java))
        }

        registerTextView.setOnClickListener {
            Log.d("LoginActivity", "Navigating to Register screen.")
            startActivity(Intent(this, Register::class.java))
        }
    }

    private fun saveLoginState(user: User?) {
        if (user == null || user.id <= 0) {
            Log.e("LoginActivity", "Failed to save user session: user is null or missing ID")
            return
        }

        Log.d("LoginActivity", "Saving user session. User: $user")
        userSession.saveUser(user)
    }

    private fun navigateToMainActivity() {
        Log.d("LoginActivity", "Navigating to MainActivity.")
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun validateEmail(email: String): Boolean {
        return when {
            email.isEmpty() -> {
                showToast("Email field cannot be empty.")
                Log.d("LoginActivity", "Email validation failed: Empty email.")
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showToast("Please enter a valid email address.")
                Log.d("LoginActivity", "Email validation failed: Invalid format -> $email")
                false
            }
            else -> {
                Log.d("LoginActivity", "Email validation passed.")
                true
            }
        }
    }

    private fun validatePassword(password: String): Boolean {
        return when {
            password.isEmpty() -> {
                showToast("Password field cannot be empty.")
                Log.d("LoginActivity", "Password validation failed: Empty password.")
                false
            }
            password.length !in 8..15 -> {
                showToast("Password must be between 8 and 15 characters.")
                Log.d("LoginActivity", "Password validation failed: Invalid length -> ${password.length}")
                false
            }
            else -> {
                Log.d("LoginActivity", "Password validation passed.")
                true
            }
        }
    }

    private fun showToast(message: String) {
        Log.d("LoginActivity", "Showing Toast: $message")
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}