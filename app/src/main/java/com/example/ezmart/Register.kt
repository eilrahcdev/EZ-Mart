package com.example.ezmart

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.Patterns
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ezmart.api.RetrofitClient
import com.example.ezmart.models.RegisterRequest
import com.example.ezmart.models.RegisterResponse
import com.example.ezmart.models.User
import com.example.ezmart.utils.UserSession
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Register : AppCompatActivity() {

    private lateinit var userSession: UserSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userSession = UserSession(this)

        if (userSession.isLoggedIn()) {
            Log.d("RegisterActivity", "User already logged in. Redirecting to MainActivity.")
            navigateToMainActivity()
            return
        }

        setContentView(R.layout.register_activity)
        enableEdgeToEdge()
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
        val registerBtn = findViewById<Button>(R.id.registerBtn)
        val loginTv = findViewById<TextView>(R.id.loginTv_register)

        val spannable = SpannableString("Already have an account? Login")
        spannable.setSpan(
            ForegroundColorSpan(Color.BLUE),
            spannable.indexOf("Login"),
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        loginTv.text = spannable

        registerBtn.setOnClickListener {
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
                val registerRequest = RegisterRequest(firstName, lastName, email, password, confirmPassword, contact, gender, birthdate, address)

                RetrofitClient.instance.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
                    override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                        val registerResponse = response.body()

                        if (response.isSuccessful && registerResponse?.success == true) {
                            showToast("Registration Successful! ✅")
                            saveUserData(registerResponse)
                            navigateToMainActivity()
                        } else {
                            showToast(registerResponse?.message ?: "Registration failed ❌")
                        }
                    }

                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        showToast("Network error: ${t.message} ❌")
                        Log.e("RegisterActivity", "Registration API failed: ${t.message}")
                    }
                })
            }
        }

        loginTv.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }

    }

    private fun validateInput(email: String, password: String, confirmPassword: String, firstName: String, lastName: String, birthdate: String, phone: String, address: String, gender: String): Boolean {
        return when {
            email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showToast("Please enter a valid email address ❌"); false
            }
            password.length !in 8..15 -> {
                showToast("Password must be between 8 and 15 characters ❌"); false
            }
            password != confirmPassword -> {
                showToast("Passwords do not match ❌"); false
            }
            firstName.isEmpty() || !firstName.matches(Regex("^[a-zA-Z]+$")) -> {
                showToast("First name must contain only letters ❌"); false
            }
            lastName.isEmpty() || !lastName.matches(Regex("^[a-zA-Z]+$")) -> {
                showToast("Last name must contain only letters ❌"); false
            }
            birthdate.isEmpty() -> {
                showToast("Birthdate is required ❌"); false
            }
            phone.isEmpty() || !phone.matches(Regex("^[0-9]+$")) -> {
                showToast("Phone number must contain only digits ❌"); false
            }
            address.isEmpty() -> {
                showToast("Address is required ❌"); false
            }
            gender == "Select a gender" -> {
                showToast("Please select a valid gender ❌"); false
            }
            else -> true
        }
    }

    private fun saveUserData(response: RegisterResponse) {
        val user = User(
            id = response.id,
            first_name = response.firstName,
            last_name = response.lastName,
            email = response.email,
            birthdate = response.birthdate,
            contact = response.contact,
            address = response.address,
            gender = response.gender,
            fcmToken = null // Can be updated later
        )
        userSession.saveUser(user)
        Log.d("RegisterActivity", "User session saved: ${response.id}")
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}