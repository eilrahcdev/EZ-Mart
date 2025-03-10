package com.example.ezmart

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CartManager {

    private const val CART_PREFS = "cart_prefs"
    private const val CART_KEY = "cart"

    fun getCart(context: Context): List<Product> {
        val sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(CART_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<Product>>() {}.type
            Gson().fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun addToCart(context: Context, product: Product) {
        val cartList = getCart(context).toMutableList()

        // Check if the product already exists in the cart
        val existingProduct = cartList.find { it.name == product.name }

        if (existingProduct != null) {
            // Increase quantity if the product already exists
            existingProduct.quantity += product.quantity
        } else {
            // Add new product if not found
            cartList.add(product)
        }

        saveCart(context, cartList)
    }

    fun clearCart(context: Context) {
        val sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(CART_KEY).apply()
    }

    private fun saveCart(context: Context, cartList: List<Product>) {
        val sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(cartList)
        editor.putString(CART_KEY, json)
        editor.apply()
    }
}
