package com.example.ezmart

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CartManager {

    private const val CART_PREFS = "cart_prefs"
    private const val CART_KEY = "cart"

    fun getCart(context: Context): MutableList<Product> {
        val sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(CART_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<Product>>() {}.type
            Gson().fromJson(json, type)
        } else {
            mutableListOf()
        }
    }

    fun removeFromCart(context: Context, product: Product) {
        val cartList = getCart(context)
        cartList.removeAll { it.id == product.id } // Remove by ID (Ensures accuracy)
        saveCart(context, cartList)
    }

    fun removeMultipleFromCart(context: Context, products: List<Product>) {
        val cartList = getCart(context)
        val productIdsToRemove = products.map { it.id }
        cartList.removeAll { it.id in productIdsToRemove } // Remove selected products
        saveCart(context, cartList)
    }

    fun addToCart(context: Context, product: Product) {
        val cartList = getCart(context)

        // Ensure product quantity is at least 1
        if (product.quantity <= 0) {
            product.quantity = 1
        }

        // Check if the product already exists in the cart
        val existingProduct = cartList.find { it.id == product.id }

        if (existingProduct != null) {
            existingProduct.quantity += product.quantity // Increase quantity if exists
        } else {
            cartList.add(product) // Add new product if not found
        }

        saveCart(context, cartList)
    }

    fun updateCart(context: Context, updatedCart: List<Product>) {
        saveCart(context, updatedCart) // Saves modified cart list
    }

    fun clearCart(context: Context) {
        val sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(CART_KEY).apply()
    }

    fun saveCart(context: Context, cartList: List<Product>) {
        val sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(cartList)
        editor.putString(CART_KEY, json)
        editor.apply()
    }
}
