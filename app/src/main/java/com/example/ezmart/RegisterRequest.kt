package com.example.ezmart.models

data class RegisterRequest(
    val first_name: String,
    val last_name: String,
    val email: String,
    val password: String,
    val confirm_password: String,
    val contact: String,
    val gender: String,
    val birthdate: String,
    val address: String?
)
