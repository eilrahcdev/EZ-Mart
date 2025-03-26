package com.example.ezmart.api

import com.google.gson.annotations.SerializedName

data class StockUpdateRequest(
    val action: String,
    val products: List<ProductStock>
)

data class ProductStock(
    @SerializedName("product_id") val productId: Int,
    val quantity: Int
)
