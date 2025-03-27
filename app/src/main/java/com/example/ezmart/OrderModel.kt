package com.example.ezmart.models

import com.example.ezmart.Product

// Order data model
data class OrderModel(
    val id: String,
    val email: String,
    val customerName: String,
    val products: List<Product>,
    val totalAmount: Double,
    val status: String,
    val paymentMethod: String
)

// API request model for placing an order
data class OrderRequest(
    val action: String = "place",
    val email: String,
    val customer_name: String,
    val total_price: Double,
    val items: List<OrderItem>
)

// Individual items inside an order
data class OrderItem(
    val id: Int,
    val name: String,
    val price: Double,
    val quantity: Int
)

// Response model for handling API responses
data class OrderResponse(
    val success: Boolean,
    val order_id: Int?,
    val error: String?
)
