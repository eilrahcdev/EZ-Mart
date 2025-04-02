package com.example.ezmart.models

data class NotificationModel(
    val id: Int,
    val user_id: Int,
    val message: String,
    val status: String,
    val created_at: String
)

