package com.example.ezmart

import android.content.Context

object CartManager {
    private const val PREFS_NAME = "cart_prefs"
    private const val CART_KEY = "cart_items"

    fun addToCart(context: Context, product: Product) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cartItems = getCart(context).toMutableList()

        cartItems.add(product)  // Add new product to cart

        val editor = sharedPreferences.edit()
        editor.putString(CART_KEY, Product.serializeList(cartItems)) // Save updated list
        editor.apply()
    }

    fun getCart(context: Context): List<Product> {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cartJson = sharedPreferences.getString(CART_KEY, "[]") ?: "[]"
        return Product.deserializeList(cartJson)  // Convert JSON back to List<Product>
    }

    fun clearCart(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(CART_KEY).apply() // Clear stored cart
    }
}

