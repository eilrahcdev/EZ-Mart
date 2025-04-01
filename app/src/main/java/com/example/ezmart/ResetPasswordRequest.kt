package com.example.ezmart.auth

data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    val new_password: String
)