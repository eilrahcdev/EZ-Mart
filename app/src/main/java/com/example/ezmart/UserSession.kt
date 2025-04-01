package com.example.ezmart.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.ezmart.models.User

class UserSession(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    fun saveUser(user: User) {
        with(editor) {
            putInt("id", user.id) // Store id as an Integer
            putString("first_name", user.first_name)
            putString("last_name", user.last_name)
            putString("email", user.email)
            putString("birthdate", user.birthdate)
            putString("contact", user.contact)
            putString("address", user.address)
            putString("gender", user.gender)
            putString("fcm_token", user.fcmToken ?: "") // Avoid null issues
            apply()
        }
    }

    // Retrieve user data
    fun getUser(): User? {
        val id = prefs.getInt("id", -1) // Get id as an Integer
        if (id == -1) return null // If id is not found, return null

        return User(
            id = id,
            first_name = prefs.getString("first_name", "") ?: "",
            last_name = prefs.getString("last_name", "") ?: "",
            email = prefs.getString("email", "") ?: "",
            birthdate = prefs.getString("birthdate", "") ?: "",
            contact = prefs.getString("contact", "") ?: "",
            address = prefs.getString("address", "") ?: "",
            gender = prefs.getString("gender", "") ?: "",
            fcmToken = prefs.getString("fcm_token", "")
        )
    }

    // Save or update the FCM token separately
    fun saveFcmToken(token: String) {
        editor.putString("fcm_token", token).apply()
    }

    // Retrieve FCM token
    fun getFcmToken(): String? = prefs.getString("fcm_token", null)

    // Check if user is logged in
    fun isLoggedIn(): Boolean = prefs.contains("id")

    // Clear all session data
    fun clearSession() {
        editor.clear().apply()
    }

    // Update user data
    fun updateUser(updatedUser: User) {
        saveUser(updatedUser)
    }

    // Update specific fields
    fun updateFirstName(firstName: String) {
        editor.putString("first_name", firstName).apply()
    }

    fun updateEmail(email: String) {
        editor.putString("email", email).apply()
    }
}
