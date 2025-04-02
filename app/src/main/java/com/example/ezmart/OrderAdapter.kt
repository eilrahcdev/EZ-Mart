package com.example.ezmart

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ezmart.api.RetrofitClient
import com.example.ezmart.models.*
import com.example.ezmart.utils.UserSession
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderAdapter(
    private var orderList: MutableList<OrderModel>,
    private val context: Context, // Ensure Context comes first
    private val orderUpdateListener: OrderUpdateListener
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productDetails: TextView = view.findViewById(R.id.product_name)
        val totalAmount: TextView = view.findViewById(R.id.total_amount)
        val paymentMethod: TextView = view.findViewById(R.id.payment_method)
        val payButton: Button = view.findViewById(R.id.pay_button)
        val cancelButton: Button = view.findViewById(R.id.cancel_button)
        val receiveButton: Button = view.findViewById(R.id.order_received_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]

        // Construct product details
        holder.productDetails.text = order.items.joinToString("\n") {
            "• ${it.name} (Qty: ${it.quantity})"
        }
        holder.totalAmount.text = "Total: ₱${String.format("%.2f", order.totalPrice.toDouble())}"
        holder.paymentMethod.text = "Payment: ${order.paymentMethod}"

        val userSession = UserSession(context)
        val user = userSession.getUser()

        // Show Pay button only for "To Pay" orders (excluding Cash on Pick-up)
        holder.payButton.visibility = if (order.status == "To Pay" && order.paymentMethod != "Cash on Pick-up") {
            View.VISIBLE
        } else {
            View.GONE
        }
        holder.payButton.setOnClickListener {
            user?.let { onPayButtonClicked(order, it) }
        }

        // Show Cancel button only for "Pending" orders
        holder.cancelButton.visibility = if (order.status == "Pending") {
            View.VISIBLE
        } else {
            View.GONE
        }
        holder.cancelButton.setOnClickListener {
            user?.let { onCancelButtonClicked(order, position) }
        }

        // Show Receive button only if order status is "Paid"
        holder.receiveButton.visibility = if (order.status == "Ready to Pick Up") {
            View.VISIBLE
        } else {
            View.GONE
        }
        holder.receiveButton.setOnClickListener {
            onReceiveOrderClicked(order, position)
        }
    }

    override fun getItemCount(): Int = orderList.size

    fun updateOrders(newOrders: List<OrderModel>) {
        orderList.clear()
        orderList.addAll(newOrders)
        notifyDataSetChanged()
    }

    private fun onPayButtonClicked(order: OrderModel, user: User) {
        val paymentData = PaymentData(
            customer_name = "${user.first_name} ${user.last_name}",
            user_id = user.id.toString(),
            order_id = order.id.toString(),
            payment_method = order.paymentMethod,
            total_price = order.totalPrice,
            fcm_token = user.fcmToken ?: ""
        )

        RetrofitClient.instance.processPayment(paymentData).enqueue(object : Callback<PaymentResponse> {
            override fun onResponse(call: Call<PaymentResponse>, response: Response<PaymentResponse>) {
                if (response.isSuccessful) {
                    val checkoutUrl = response.body()?.checkoutUrl
                    if (!checkoutUrl.isNullOrEmpty()) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(checkoutUrl))
                        context.startActivity(intent)
                    } else {
                        showToast("Payment failed: Invalid response")
                    }
                } else {
                    showToast("Payment failed: Server error")
                }
            }

            override fun onFailure(call: Call<PaymentResponse>, t: Throwable) {
                showToast("Error: ${t.message}")
            }
        })
    }

    private fun onCancelButtonClicked(order: OrderModel, position: Int) {
        RetrofitClient.instance.cancelOrder(order.id.toString()).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.success == true) {
                        order.status = "Cancelled"
                        notifyItemChanged(position)
                        showToast("Order has been cancelled")

                        // Notify the fragment/activity
                        orderUpdateListener.onOrderCancelled(order)
                    } else {
                        showToast(apiResponse?.message ?: "Failed to cancel order")
                    }
                } else {
                    showToast("Failed to cancel order: Server error")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                showToast("Service Unavailable")
            }
        })
    }

    private fun onReceiveOrderClicked(order: OrderModel, position: Int) {
        RetrofitClient.instance.updateOrderStatus(order.id.toString(), "Completed")
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        if (apiResponse?.success == true) {
                            order.status = "Completed"
                            notifyItemChanged(position)
                            showToast("Order received successfully")

                            orderUpdateListener.onOrderCompleted(order)
                        } else {
                            showToast(apiResponse?.message ?: "Failed to update order")
                        }
                    } else {
                        showToast("Failed to update order: Server error")
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    showToast("Service Unavailable")
                }
            })
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
