package com.example.ezmart.models

data class User(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val email: String,
    val contact: String,
    val gender: String,
    val birthdate: String,
    val address: String,
    val fcmToken: String?
)
