package com.example.ezmart.models

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val user: User?
)

data class User(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val email: String,
    val birthdate: String,
    val contact: String,
    val address: String,
    val gender: String,
    val fcmToken: String? = null
)
