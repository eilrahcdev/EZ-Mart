package com.example.ezmart

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.ezmart.api.RetrofitClient
import com.example.ezmart.models.ProfileResponse
import com.example.ezmart.utils.UserSession
import com.example.ezmart.viewmodels.ProfileViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Profile : AppCompatActivity() {
    private lateinit var userSession: UserSession
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userSession = UserSession(this)

        val user = userSession.getUser()
        if (user == null) {
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

        observeProfileData()
        profileViewModel.fetchProfile(user.email) // Fetch profile on start

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

    private fun observeProfileData() {
        profileViewModel.userLiveData.observe(this, Observer { profile ->
            findViewById<TextView>(R.id.firstnameTv).text = profile.first_name
            findViewById<TextView>(R.id.lastnameTv).text = profile.last_name
            findViewById<TextView>(R.id.emailTv).text = profile.email
            findViewById<TextView>(R.id.dateofbirthTv).text = profile.birthdate
            findViewById<TextView>(R.id.phonenumberTv).text = profile.contact
            findViewById<TextView>(R.id.addressTv).text = profile.address
            findViewById<TextView>(R.id.genderTv).text = profile.gender
        })
    }

    private fun loadUserData() {
        val user = userSession.getUser()

        if (user == null) {
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }

        findViewById<TextView>(R.id.firstnameTv).text = user.first_name
        findViewById<TextView>(R.id.lastnameTv).text = user.last_name
        findViewById<TextView>(R.id.emailTv).text = user.email
        findViewById<TextView>(R.id.dateofbirthTv).text = user.birthdate
        findViewById<TextView>(R.id.phonenumberTv).text = user.contact
        findViewById<TextView>(R.id.addressTv).text = user.address
        findViewById<TextView>(R.id.genderTv).text = user.gender
    }

    private fun showEditProfileDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.edit_profile_dialog)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(true)

        val firstNameEt = dialog.findViewById<EditText>(R.id.firstnameEt_edit)
        val lastNameEt = dialog.findViewById<EditText>(R.id.lastnameEt_edit)
        val birthdateEt = dialog.findViewById<EditText>(R.id.birthdateEt_edit)
        val phoneEt = dialog.findViewById<EditText>(R.id.phonenumEt_edit)
        val addressEt = dialog.findViewById<EditText>(R.id.addressEt_edit)
        val genderSpinner = dialog.findViewById<Spinner>(R.id.genderSpinner_edit)

        // Get latest profile data from ViewModel
        profileViewModel.userLiveData.value?.let { user ->
            firstNameEt.setText(user.first_name)
            lastNameEt.setText(user.last_name)
            birthdateEt.setText(user.birthdate ?: "")
            phoneEt.setText(user.contact ?: "")
            addressEt.setText(user.address ?: "")

            val genderArray = resources.getStringArray(R.array.gender_array)
            val genderIndex = genderArray.indexOf(user.gender ?: "")
            genderSpinner.setSelection(if (genderIndex >= 0) genderIndex else 0)
        }

        dialog.findViewById<Button>(R.id.saveButton).setOnClickListener {
            updateProfile(
                firstNameEt.text.toString().trim(),
                lastNameEt.text.toString().trim(),
                birthdateEt.text.toString().trim(),
                phoneEt.text.toString().trim(),
                addressEt.text.toString().trim(),
                genderSpinner.selectedItem.toString()
            )
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.cancelButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showLogoutDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.logout_dialogbox)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)

        dialog.findViewById<Button>(R.id.btn_yes).setOnClickListener {
            dialog.dismiss()
            logoutUser(this)
        }

        dialog.findViewById<Button>(R.id.btn_no).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateProfile(firstName: String, lastName: String, birthdate: String, contact: String, address: String, gender: String) {
        val user = userSession.getUser()
        user?.let {
            RetrofitClient.instance.updateProfile(it.email, firstName, lastName, birthdate, contact, address, gender)
                .enqueue(object : Callback<ProfileResponse> {
                    override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            val updatedUser = it.copy(
                                first_name = firstName,
                                last_name = lastName,
                                birthdate = birthdate,
                                contact = contact,
                                address = address,
                                gender = gender
                            )
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

    private fun logoutUser(context: Context) {
        userSession.clearSession()
        val intent = Intent(context, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        val user = userSession.getUser()
        user?.let { profileViewModel.fetchProfile(it.email) }
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
