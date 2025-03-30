package com.example.ezmart.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.ezmart.models.User

class UserSession(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    // Save user data including FCM token
    fun saveUser(user: User) {
        with(editor) {
            putString("id", user.id.toString())
            putString("first_name", user.first_name)
            putString("last_name", user.last_name)
            putString("email", user.email)
            putString("birthdate", user.birthdate)
            putString("contact", user.contact)
            putString("address", user.address)
            putString("gender", user.gender)
            putString("fcm_token", user.fcmToken)
            apply() // Save changes asynchronously
        }
    }

    // Retrieve user data
    fun getUser(): User? {
        val idString = prefs.getString("id", null) ?: return null
        val id = idString.toIntOrNull() ?: return null

        return User(
            id,
            prefs.getString("first_name", "") ?: "",
            prefs.getString("last_name", "") ?: "",
            prefs.getString("email", "") ?: "",
            prefs.getString("birthdate", "") ?: "",
            prefs.getString("contact", "") ?: "",
            prefs.getString("address", "") ?: "",
            prefs.getString("gender", "") ?: "",
            prefs.getString("fcm_token", null)
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

    // Clear all session data, including user data and FCM token
    fun clearSession() {
        editor.clear().apply()
    }

    // Update specific user details
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
