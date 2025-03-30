package com.example.ezmart.models

import com.example.ezmart.Product
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

// Main order model used for API responses
data class OrderModel(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("customer_name") val customerName: String,
    @SerializedName("total_price") val totalPrice: Double,
    @SerializedName("status") var status: String,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("fcm_token") val fcmToken: String?,
    @SerializedName("items") val items: List<OrderItem>,
    @SerializedName("products") val products: List<Product>
) {
    @SerializedName("order_details")
    val orderDetails: String? = null

    fun getParsedOrderDetails(): List<OrderItem> {
        return if (orderDetails != null) {
            val gson = Gson()
            val type = object : TypeToken<List<OrderItem>>() {}.type
            gson.fromJson(orderDetails, type)
        } else {
            emptyList()
        }
    }
}

// API request model for placing an order
data class OrderRequest(
    @SerializedName("action") val action: String = "place",
    @SerializedName("user_id") val userId: Int,
    @SerializedName("customer_name") val customerName: String,
    @SerializedName("total_price") val totalPrice: Double,
    @SerializedName("status") val status: String,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("fcm_token") val fcmToken: String? = null,
    @SerializedName("items") val items: List<OrderItem>
) {
    fun toJson(): String {
        return Gson().toJson(this)
    }
}

// API request model for canceling an order (NEWLY ADDED)
data class OrderCancelRequest(
    @SerializedName("action") val action: String = "restore_stock",
    @SerializedName("order_id") val orderId: Int,
    @SerializedName("products") val products: List<OrderItem>
) {
    fun toJson(): String {
        return Gson().toJson(this)
    }
}

// Individual items inside an order (product details)
data class OrderItem(
    @SerializedName("product_id") val productId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Double,
    @SerializedName("quantity") val quantity: Int
)

data class OrderResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("order_id") val orderId: Int?,
    @SerializedName("message") val message: String?
)

data class OrderListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("orders") val orders: List<OrderModel>
)

data class PaymentResponse(
    val success: Boolean,
    val checkoutUrl: String?
)

data class PaymentData(
    val customer_name: String,
    val user_id: String,
    val order_id: String,
    val payment_method: String,
    val total_price: Double,
    val fcm_token: String
)

data class ApiResponse(
    val success: Boolean,
    val message: String
)
