package com.example.ezmart.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.ezmart.models.User

class UserSession(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    fun saveUser(user: User) {
        Log.d("UserSession", "Saving user: ID=${user.id}, Email=${user.email}")

        if (user.id <= 0) {
            Log.e("UserSession", "Error: Attempting to save an invalid user ID (${user.id})")
            return
        }
        saveUserId(user.id) // Save ID separately for reliability

        with(editor) {
            putString("first_name", user.first_name)
            putString("last_name", user.last_name)
            putString("email", user.email)
            putString("birthdate", user.birthdate)
            putString("contact", user.contact)
            putString("address", user.address)
            putString("gender", user.gender)
            putString("fcm_token", user.fcmToken ?: "")
            apply()
        }
    }


    // Save the user ID separately for better reliability
    fun saveUserId(userId: Int) {
        if (userId > 0) {
            editor.putInt("user_id", userId).apply()
            Log.d("UserSession", "User ID successfully saved: $userId")
        } else {
            Log.e("UserSession", "Error: Attempting to save an invalid user ID ($userId)")
        }
    }

    // Retrieve the user ID
    fun getUserId(): Int {
        val userId = prefs.getInt("user_id", -1)
        Log.d("UserSession", "Retrieved User ID: $userId")
        return userId
    }

    // Retrieve user data
    fun getUser(): User? {
        val id = getUserId()
        Log.d("UserSession", "Retrieved User ID in getUser(): $id")
        if (id <= 0) return null

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
    fun isLoggedIn(): Boolean = getUserId() != -1

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
