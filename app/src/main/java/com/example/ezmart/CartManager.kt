package com.example.ezmart

import android.content.Context
import com.example.ezmart.utils.UserSession
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CartManager {

    private const val CART_PREFS = "cart_prefs"

    // Get a unique cart key based on the logged-in user's email
    private fun getCartKey(context: Context): String {
        val userSession = UserSession(context)
        val userEmail = userSession.getUser()?.email ?: "guest" // Default for guest users
        return "cart_$userEmail" // Cart is stored per user
    }

    // Retrieve cart for the current user
    fun getCart(context: Context): MutableList<Product> {
        val sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE)
        val cartKey = getCartKey(context) // Get the cart key for the logged-in user
        val json = sharedPreferences.getString(cartKey, null)

        return if (json != null) {
            val type = object : TypeToken<MutableList<Product>>() {}.type
            Gson().fromJson(json, type)
        } else {
            mutableListOf()
        }
    }

    // Save cart for the current user
    fun saveCart(context: Context, cartList: List<Product>) {
        val sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val cartKey = getCartKey(context) // Use unique key per user
        val json = Gson().toJson(cartList)

        editor.putString(cartKey, json)
        editor.apply()
    }

    fun addToCart(context: Context, product: Product, quantity: Int) {
        val cartList = getCart(context)
        val existingProduct = cartList.find { it.id == product.id }

        if (existingProduct != null) {
            existingProduct.quantity += quantity
        } else {
            product.quantity = quantity
            cartList.add(product)
        }

        saveCart(context, cartList)
    }

    fun updateCart(context: Context, updatedCart: List<Product>) {
        saveCart(context, updatedCart) // Saves modified cart list
    }

    fun removeFromCart(context: Context, product: Product) {
        val cartList = getCart(context)
        cartList.removeAll { it.id == product.id }
        saveCart(context, cartList)
    }

    fun clearCart(context: Context) {
        val sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val cartKey = getCartKey(context) // Clear only the logged-in user's cart
        editor.remove(cartKey).apply()
    }
}
