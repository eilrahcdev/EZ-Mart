// ResetPasswordRequest.kt
package com.example.ezmart.auth

data class ResetPasswordRequest(
    val email: String,
    val otp: String,  // Include OTP in the reset request
    val new_password: String
)
