package com.example.ezmart.models

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val contact: String,
    val gender: String,
    val birthdate: String,
    val address: String
)
