package com.example.ezmart

import android.content.Context
import com.example.ezmart.models.OrderModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.collections.toMutableList

object OrderManager {
    private const val ORDERS_PREF = "orders_pref"

    fun addOrder(order: OrderModel, context: Context) {
        val sharedPreferences = context.getSharedPreferences(ORDERS_PREF, Context.MODE_PRIVATE)
        val orders = getOrders(context).toMutableList()
        orders.add(order)
        sharedPreferences.edit().putString("orders", Gson().toJson(orders)).apply()
    }

    fun getOrders(context: Context): List<OrderModel> {
        val sharedPreferences = context.getSharedPreferences(ORDERS_PREF, Context.MODE_PRIVATE)
        val ordersJson = sharedPreferences.getString("orders", "[]")
        return Gson().fromJson(ordersJson, object : TypeToken<List<OrderModel>>() {}.type)
    }
}
