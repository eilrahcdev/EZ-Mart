package com.example.ezmart

import android.content.Context
import com.example.ezmart.models.OrderModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object OrderManager {
    private const val ORDERS_PREF = "orders_pref"
    private const val USER_PREFS = "UserPrefs"

    fun addOrder(order: OrderModel, context: Context) {
        val sharedPreferences = context.getSharedPreferences(ORDERS_PREF, Context.MODE_PRIVATE)
        val orders = getOrders(context).toMutableList()
        orders.add(order)
        val ordersJson = Gson().toJson(orders)
        sharedPreferences.edit().putString("orders", ordersJson).apply()

        println("Orders saved: $ordersJson")
    }


    fun getOrders(context: Context): List<OrderModel> {
        val sharedPreferences = context.getSharedPreferences(ORDERS_PREF, Context.MODE_PRIVATE)
        val userPrefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)

        val loggedInEmail = userPrefs.getString("loggedInUser", null)
        println("Logged-in user email: $loggedInEmail")  // Debugging

        if (loggedInEmail.isNullOrEmpty()) {
            return emptyList()
        }

        val ordersJson = sharedPreferences.getString("orders", "[]")
        val allOrders: List<OrderModel> = Gson().fromJson(ordersJson, object : TypeToken<List<OrderModel>>() {}.type)

        return allOrders.filter { it.email == loggedInEmail }
    }
}