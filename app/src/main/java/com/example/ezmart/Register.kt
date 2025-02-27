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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class Register : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

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
            val phone = phoneEt.text.toString().trim()
            val address = addressEt.text.toString().trim()
            val gender = genderSpinner.selectedItem.toString()

            if (validateInput(email, password, confirmPassword, firstName, lastName, birthdate, phone, address, gender)) {
                Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show()
                // Proceed with signup logic
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
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
