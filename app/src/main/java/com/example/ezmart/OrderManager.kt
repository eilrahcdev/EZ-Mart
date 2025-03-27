package com.example.ezmart

import android.content.Context
import com.example.ezmart.api.RetrofitClient
import com.example.ezmart.models.OrderModel
import com.example.ezmart.models.OrderResponse
import com.example.ezmart.models.UpdateStatusRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object OrderManager {
    private const val PREFS_NAME = "orders_prefs"
    private const val ORDERS_KEY = "user_orders"

    fun saveOrders(context: Context, orders: List<OrderModel>) {
        val sharedPrefs = context.getSharedPreferences("orders_pref", Context.MODE_PRIVATE)
        val json = Gson().toJson(orders)
        sharedPrefs.edit().putString("orders", json).apply()
    }

    fun getOrders(context: Context): List<OrderModel> {
        val sharedPrefs = context.getSharedPreferences("orders_pref", Context.MODE_PRIVATE)
        val json = sharedPrefs.getString("orders", "[]") ?: "[]"
        return Gson().fromJson(json, object : TypeToken<List<OrderModel>>() {}.type)
    }

    fun fetchAndSaveOrders(context: Context, customerName: String, callback: (Boolean) -> Unit) {
        RetrofitClient.instance.getOrders(customerName).enqueue(object : Callback<List<OrderModel>> {
            override fun onResponse(call: Call<List<OrderModel>>, response: Response<List<OrderModel>>) {
                if (response.isSuccessful) {
                    response.body()?.let { orders ->
                        saveOrders(context, orders)
                        callback(true)
                    } ?: callback(false)
                } else {
                    callback(false)
                }
            }

            override fun onFailure(call: Call<List<OrderModel>>, t: Throwable) {
                callback(false)
            }
        })
    }

    fun updateOrderStatus(orderId: Int, newStatus: String, callback: (Boolean) -> Unit) {
        RetrofitClient.instance.updateOrderStatus(UpdateStatusRequest(orderId.toString(), newStatus))
            .enqueue(object : Callback<OrderResponse> {
                override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                    callback(response.isSuccessful() && response.body()?.success == true)
                }

                override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                    callback(false)
                }
            })
    }
}