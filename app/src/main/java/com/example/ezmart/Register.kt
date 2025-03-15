package com.example.ezmart

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ezmart.api.ApiClient
import com.example.ezmart.models.RegisterRequest
import com.example.ezmart.models.RegisterResponse
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Register : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if the user is already registered
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val isRegistered = sharedPreferences.getBoolean("isRegistered", false)

        if (isRegistered) {
            // If user is already registered, skip register activity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.register_activity)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val emailEt = findViewById<TextInputEditText>(R.id.emailEt_register)
        val passwordEt = findViewById<TextInputEditText>(R.id.passwordEt_register)
        val confirmPasswordEt = findViewById<TextInputEditText>(R.id.confirmpasswordEt_register)
        val firstNameEt = findViewById<TextInputEditText>(R.id.firstnameEt_register)
        val lastNameEt = findViewById<TextInputEditText>(R.id.lastnameEt_register)
        val birthdateEt = findViewById<TextInputEditText>(R.id.birthdateEt_register)
        val phoneEt = findViewById<TextInputEditText>(R.id.phonenumEt_register)
        val addressEt = findViewById<TextInputEditText>(R.id.addressEt_register)
        val genderSpinner = findViewById<Spinner>(R.id.genderSpinner)
        val signupBtn = findViewById<Button>(R.id.registerBtn)
        val loginTv = findViewById<TextView>(R.id.loginTv_register)

        // Apply SpannableString to loginTv
        val spannable = SpannableString("Already have an account? Login")
        spannable.setSpan(
            ForegroundColorSpan(Color.BLUE),
            spannable.indexOf("Login"),
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        loginTv.text = spannable

        // Gender selection validation function
        fun validateGenderSelection(position: Int): Boolean {
            return if (position == 0) {
                false
            } else {
                true
            }
        }

        // Initialize Spinner with validation
        genderSpinner.setSelection(0)
        genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                validateGenderSelection(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        signupBtn.setOnClickListener {
            val email = emailEt.text.toString().trim()
            val password = passwordEt.text.toString().trim()
            val confirmPassword = confirmPasswordEt.text.toString().trim()
            val firstName = firstNameEt.text.toString().trim()
            val lastName = lastNameEt.text.toString().trim()
            val birthdate = birthdateEt.text.toString().trim()
            val contact = phoneEt.text.toString().trim()
            val address = addressEt.text.toString().trim()
            val gender = genderSpinner.selectedItem.toString()

            if (validateInput(email, password, confirmPassword, firstName, lastName, birthdate, contact, address, gender)) {
                val registerRequest = RegisterRequest(firstName, lastName, email, password, confirmPassword, gender, birthdate, contact, address)

                ApiClient.instance.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
                    override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null && responseBody.success) {
                                Toast.makeText(this@Register, "Registration Successful!", Toast.LENGTH_SHORT).show()

                                // Save registration flag in SharedPreferences
                                val editor = sharedPreferences.edit()
                                editor.putBoolean("isRegistered", true)
                                editor.apply()

                                startActivity(Intent(this@Register, MainActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this@Register, "Registration failed: ${responseBody?.message ?: "No message"}", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Toast.makeText(this@Register, "Error: $errorBody", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        Toast.makeText(this@Register, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        // Handle navigation for Login button
        loginTv.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }

    private fun validateInput(
        email: String,
        password: String,
        confirmPassword: String,
        firstName: String,
        lastName: String,
        birthdate: String,
        phone: String,
        address: String,
        gender: String
    ): Boolean {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email address")
            return false
        }
        if (password.length !in 8..15) {
            showToast("Password must be between 8 and 15 characters")
            return false
        }
        if (password != confirmPassword) {
            showToast("Passwords do not match")
            return false
        }
        if (firstName.isEmpty() || !firstName.matches(Regex("^[a-zA-Z]+$"))) {
            showToast("First name must contain only letters")
            return false
        }
        if (lastName.isEmpty() || !lastName.matches(Regex("^[a-zA-Z]+$"))) {
            showToast("Last name must contain only letters")
            return false
        }
        if (birthdate.isEmpty()) {
            showToast("Birthdate is required")
            return false
        }
        if (phone.isEmpty() || !phone.matches(Regex("^[0-9]+$"))) {
            showToast("Phone number must contain only digits")
            return false
        }
        if (address.isEmpty()) {
            showToast("Address is required")
            return false
        }
        if (gender == "Select a gender") {
            showToast("Please select a valid gender")
            return false
        }
        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}