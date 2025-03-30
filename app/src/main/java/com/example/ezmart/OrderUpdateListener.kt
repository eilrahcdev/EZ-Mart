package com.example.ezmart

import com.example.ezmart.models.OrderModel

interface OrderUpdateListener {
    fun onOrderCancelled(order: OrderModel)
}
