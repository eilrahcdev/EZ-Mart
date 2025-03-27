package com.example.ezmart.models

import com.example.ezmart.Product
import com.google.gson.annotations.SerializedName

data class OrderModel(
    @SerializedName("id") val id: String = "",
    @SerializedName("email") val email: String,
    @SerializedName("customer_name") val customerName: String,
    @SerializedName("items") val products: List<Product>,
    @SerializedName("total_price") val totalAmount: Double,
    @SerializedName("status") val status: String = "Pending",
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("fcm_token") val fcmToken: String? = null,
    @SerializedName("action") val action: String = "place"  // Add this line
)

data class OrderResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("order_id") val orderId: Int? = null,
    @SerializedName("error") val error: String? = null
)

data class UpdateStatusRequest(
    @SerializedName("id") val orderId: String,
    @SerializedName("status") val status: String
)