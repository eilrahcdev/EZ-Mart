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
import com.example.ezmart.utils.UserSession
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Profile : AppCompatActivity() {
    private lateinit var userSession: UserSession

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userSession = UserSession(this)
        val user = userSession.getUser()

        if (user == null) {
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

        loadUserData()

        findViewById<ImageButton>(R.id.backBtn_profile).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.logoutBtn).setOnClickListener {
            showLogoutDialog()
        }

        findViewById<ImageButton>(R.id.editprofileIbtn).setOnClickListener {
            showEditProfileDialog()
        }

        setupNavigation()
    }

    private fun loadUserData() {
        val user = userSession.getUser()

        if (user == null) {
            Log.d("ProfileActivity", "User data is null, redirecting to login.")
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }

        Log.d("ProfileActivity", "User Data Loaded: ${user.first_name}, ${user.email}")

        findViewById<TextView>(R.id.firstnameTv).text = user.first_name
        findViewById<TextView>(R.id.lastnameTv).text = user.last_name
        findViewById<TextView>(R.id.emailTv).text = user.email
        findViewById<TextView>(R.id.dateofbirthTv).text = user.birthdate
        findViewById<TextView>(R.id.phonenumberTv).text = user.contact
        findViewById<TextView>(R.id.addressTv).text = user.address
        findViewById<TextView>(R.id.genderTv).text = user.gender
    }


    // Show edit profile dialog
    private fun showEditProfileDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.edit_profile_dialog)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(true)

        val userSession = UserSession(this)
        val user = userSession.getUser()

        if (user != null) {
            val firstNameEt = dialog.findViewById<TextInputEditText>(R.id.firstnameEt_edit)
            val lastNameEt = dialog.findViewById<TextInputEditText>(R.id.lastnameEt_edit)
            val birthdateEt = dialog.findViewById<TextInputEditText>(R.id.birthdateEt_edit)
            val phoneEt = dialog.findViewById<TextInputEditText>(R.id.phonenumEt_edit)
            val addressEt = dialog.findViewById<TextInputEditText>(R.id.addressEt_edit)
            val genderSpinner = dialog.findViewById<Spinner>(R.id.genderSpinner_edit)

            // Pre-fill fields with current user data
            firstNameEt.setText(user.first_name)
            lastNameEt.setText(user.last_name)
            birthdateEt.setText(user.birthdate)
            phoneEt.setText(user.contact)
            addressEt.setText(user.address)

            val genderArray = resources.getStringArray(R.array.gender_array)
            val genderIndex = genderArray.indexOf(user.gender)
            genderSpinner.setSelection(if (genderIndex >= 0) genderIndex else 0)

            dialog.findViewById<Button>(R.id.saveButton).setOnClickListener {
                val updatedUser = user.copy(
                    first_name = firstNameEt.text.toString().trim(),
                    last_name = lastNameEt.text.toString().trim(),
                    birthdate = birthdateEt.text.toString().trim(),
                    contact = phoneEt.text.toString().trim(),
                    address = addressEt.text.toString().trim(),
                    gender = genderSpinner.selectedItem.toString()
                )

                userSession.updateUser(updatedUser)

                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            dialog.findViewById<Button>(R.id.cancelButton).setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        } else {
            Toast.makeText(this, "User data not found!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateProfile(firstName: String, lastName: String, birthdate: String, contact: String, address: String, gender: String) {
        val user = userSession.getUser()
        user?.let {
            RetrofitClient.instance.updateProfile(it.email, firstName, lastName, birthdate, contact, address, gender)
                .enqueue(object : Callback<ProfileResponse> {
                    override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            // Create a new updated user object
                            val updatedUser = it.copy(
                                first_name = firstName,
                                last_name = lastName,
                                birthdate = birthdate,
                                contact = contact,
                                address = address,
                                gender = gender
                            )

                            // Save updated user session
                            userSession.updateUser(updatedUser)

                            loadUserData()
                            Toast.makeText(this@Profile, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@Profile, "Failed to update profile", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                        Toast.makeText(this@Profile, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
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

        // Clear all saved user data
        editor.clear()
        editor.apply()

        // Also clear UserSession instance if you're using it
        val userSession = UserSession(context)
        userSession.clearSession()

        Log.d("ProfileActivity", "User logged out. Redirecting to Login.")

        // Redirect to Login & prevent going back to the previous activity
        val intent = Intent(context, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
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

    override fun onResume() {
        super.onResume()
        loadUserData()
    }
}