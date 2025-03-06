package com.example.ezmart

import android.content.Context

object CartManager {
    private const val PREFS_NAME = "cart_prefs"
    private const val CART_KEY = "cart_items"

    fun addToCart(context: Context, product: Product) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cartItems = getCart(context).toMutableList()

        // Clean price before saving (remove currency symbol and ensure it's Double)
        val cleanedProduct = product.copy(
            price = product.price.toString().replace("[^\\d.]", "").toDoubleOrNull() ?: 0.0
        )

        cartItems.add(cleanedProduct)  // Add new product to cart

        val editor = sharedPreferences.edit()
        editor.putString(CART_KEY, Product.serializeList(cartItems)) // Save updated list
        editor.apply()
    }

    fun getCart(context: Context): List<Product> {
        val sharedPreferences = context.getSharedPreferences("cart_prefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("cart_items", "[]") ?: "[]"

        return try {
            Product.deserializeList(json)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun clearCart(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(CART_KEY).apply() // Clear stored cart
    }
}
