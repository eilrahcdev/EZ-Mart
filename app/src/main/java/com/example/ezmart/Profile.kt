package com.example.ezmart

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ezmart.api.RetrofitClient
import com.example.ezmart.models.ProfileResponse
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Profile : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val loggedInUser = sharedPreferences.getString("loggedInUser", null)

        // Redirect to Login if the user is not logged in
        if (loggedInUser == null) {
            Log.d("ProfileActivity", "User not logged in. Redirecting to Login.")
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.profile_activity)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profile_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Load and display user data
        loadUserData(loggedInUser)

        // Back Button
        findViewById<ImageButton>(R.id.backBtn_profile).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Logout button
        findViewById<Button>(R.id.logoutBtn).setOnClickListener {
            showLogoutDialog()
        }

        // Edit Profile Button
        findViewById<ImageButton>(R.id.editprofileIbtn).setOnClickListener {
            showEditProfileDialog(loggedInUser)
        }

        // Setup bottom navigation
        setupNavigation()
    }

    // Load user data from SharedPreferences
    private fun loadUserData(email: String) {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        val firstName = sharedPreferences.getString("${email}_firstName", "First Name")
        val lastName = sharedPreferences.getString("${email}_lastName", "Last Name")
        val birthdate = sharedPreferences.getString("${email}_birthdate", "Birthdate")
        val contact = sharedPreferences.getString("${email}_contact", "Phone Number")
        val address = sharedPreferences.getString("${email}_address", "Address")
        val gender = sharedPreferences.getString("${email}_gender", "Gender")

        Log.d("ProfileActivity", "Loaded Data for $email: $firstName, $lastName, $birthdate, $contact, $address, $gender")

        findViewById<TextView>(R.id.firstnameTv).text = firstName
        findViewById<TextView>(R.id.lastnameTv).text = lastName
        findViewById<TextView>(R.id.emailTv).text = email
        findViewById<TextView>(R.id.dateofbirthTv).text = birthdate
        findViewById<TextView>(R.id.phonenumberTv).text = contact
        findViewById<TextView>(R.id.addressTv).text = address
        findViewById<TextView>(R.id.genderTv).text = gender
    }

    // Show edit profile dialog
    private fun showEditProfileDialog(email: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.edit_profile_dialog)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(true)

        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        val firstNameEt = dialog.findViewById<TextInputEditText>(R.id.firstnameEt_edit)
        val lastNameEt = dialog.findViewById<TextInputEditText>(R.id.lastnameEt_edit)
        val birthdateEt = dialog.findViewById<TextInputEditText>(R.id.birthdateEt_edit)
        val phoneEt = dialog.findViewById<TextInputEditText>(R.id.phonenumEt_edit)
        val addressEt = dialog.findViewById<TextInputEditText>(R.id.addressEt_edit)
        val genderSpinner = dialog.findViewById<Spinner>(R.id.genderSpinner_edit)

        firstNameEt.setText(sharedPreferences.getString("${email}_firstName", ""))
        lastNameEt.setText(sharedPreferences.getString("${email}_lastName", ""))
        birthdateEt.setText(sharedPreferences.getString("${email}_birthdate", ""))
        phoneEt.setText(sharedPreferences.getString("${email}_contact", ""))
        addressEt.setText(sharedPreferences.getString("${email}_address", ""))

        val genderArray = resources.getStringArray(R.array.gender_array)
        val selectedGender = sharedPreferences.getString("${email}_gender", "")
        genderSpinner.setSelection(genderArray.indexOf(selectedGender))

        dialog.findViewById<Button>(R.id.saveButton).setOnClickListener {
            val firstName = firstNameEt.text.toString().trim()
            val lastName = lastNameEt.text.toString().trim()
            val birthdate = birthdateEt.text.toString().trim()
            val contact = phoneEt.text.toString().trim()
            val address = addressEt.text.toString().trim()
            val gender = genderSpinner.selectedItem.toString()

            updateProfile(email, firstName, lastName, birthdate, contact, address, gender)
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.cancelButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateProfile(email: String, firstName: String, lastName: String, birthdate: String, contact: String, address: String, gender: String) {
        RetrofitClient.instance.updateProfile(email, firstName, lastName, birthdate, contact, address, gender)
            .enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody?.success == true) {
                            // Save the updated data in SharedPreferences
                            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                            with(sharedPreferences.edit()) {
                                putString("${email}_firstName", firstName)
                                putString("${email}_lastName", lastName)
                                putString("${email}_birthdate", birthdate)
                                putString("${email}_contact", contact)
                                putString("${email}_address", address)
                                putString("${email}_gender", gender)
                                apply() // Save changes
                            }

                            // Refresh UI with updated data
                            loadUserData(email)

                            Toast.makeText(this@Profile, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@Profile, "Failed to update profile: ${responseBody?.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@Profile, "Server error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Toast.makeText(this@Profile, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Show logout confirmation dialog
    private fun showLogoutDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.logout_dialogbox)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)

        val btnDialogYes: Button = dialog.findViewById(R.id.btn_yes)
        val btnDialogNo: Button = dialog.findViewById(R.id.btn_no)

        btnDialogYes.setOnClickListener {
            dialog.dismiss()
            logoutUser(this)
        }

        btnDialogNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // Logout function
    private fun logoutUser(context: Context) {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("loggedInUser")
        editor.apply()
        editor.clear()

        Log.d("ProfileActivity", "User logged out. Redirecting to Login.")

        val intent = Intent(context, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
        finish()
    }

    // Setup bottom navigation
    private fun setupNavigation() {
        val navigationItems = listOf(
            R.id.homeTab to Pair(R.id.homeIcon, R.id.homeText),
            R.id.categoriesTab to Pair(R.id.categoriesIcon, R.id.categoriesText),
            R.id.ordersTab to Pair(R.id.ordersIcon, R.id.ordersText),
            R.id.profileTab to Pair(R.id.profileIcon, R.id.profileText)
        )

        // Set Profile tab as active
        setDefaultProfileTab()

        navigationItems.forEach { (tabId, iconTextPair) ->
            val (iconId, textId) = iconTextPair
            findViewById<LinearLayout>(tabId).setOnClickListener {
                resetNavigationColors()
                findViewById<ImageView>(iconId).setColorFilter(
                    ContextCompat.getColor(this, R.color.blue)
                )
                findViewById<TextView>(textId).setTextColor(
                    ContextCompat.getColor(this, R.color.blue)
                )
                startActivity(Intent(this, getDestinationClass(tabId)))
            }
        }
    }

    // Highlight Profile tab as active
    private fun setDefaultProfileTab() {
        findViewById<ImageView>(R.id.profileIcon).setColorFilter(
            ContextCompat.getColor(this, R.color.blue)
        )
        findViewById<TextView>(R.id.profileText).setTextColor(
            ContextCompat.getColor(this, R.color.blue)
        )
    }

    private fun getDestinationClass(tabId: Int): Class<*> {
        return when (tabId) {
            R.id.categoriesTab -> Categories::class.java
            R.id.ordersTab -> Orders::class.java
            R.id.profileTab -> Profile::class.java
            else -> MainActivity::class.java
        }
    }

    private fun resetNavigationColors() {
        listOf(R.id.homeIcon, R.id.categoriesIcon, R.id.ordersIcon, R.id.profileIcon).forEach {
            findViewById<ImageView>(it).setColorFilter(ContextCompat.getColor(this, R.color.black))
        }
        listOf(R.id.homeText, R.id.categoriesText, R.id.ordersText, R.id.profileText).forEach {
            findViewById<TextView>(it).setTextColor(ContextCompat.getColor(this, R.color.black))
        }
    }
}
