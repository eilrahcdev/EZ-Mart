package com.example.ezmart.models

import com.example.ezmart.Product

data class ProductResponse(
    val success: Boolean,
    val products: List<Product>,
    val error: String
)
