package com.example.ezmart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ezmart.api.RetrofitClient
import com.example.ezmart.models.OrderModel
import com.example.ezmart.models.OrderListResponse
import com.example.ezmart.utils.UserSession
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CancelledOrdersFragment : Fragment(), OrderUpdateListener {

    companion object {
        fun newInstance(): CancelledOrdersFragment {
            return CancelledOrdersFragment()
        }
    }

    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var emptyTextView: TextView
    private var cancelledOrders: MutableList<OrderModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_cancelled_orders, container, false)

        // Initialize Views
        orderRecyclerView = view.findViewById(R.id.recyclerView_orders)
        emptyTextView = view.findViewById(R.id.emptyOrdersTextView)

        orderRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize Adapter & pass OrderUpdateListener
        orderAdapter = OrderAdapter(cancelledOrders, requireContext(), this)
        orderRecyclerView.adapter = orderAdapter

        // Load Cancelled Orders
        loadCancelledOrders()

        return view
    }

    // Refresh the list on fragment resume
    override fun onResume() {
        super.onResume()
        loadCancelledOrders()
    }

    private fun loadCancelledOrders() {
        val userSession = UserSession(requireContext())
        val user = userSession.getUser()

        if (user == null) {
            showEmptyMessage("Please log in to view orders")
            return
        }

        RetrofitClient.instance.getOrders(user.id.toString()).enqueue(object : Callback<OrderListResponse> {
            override fun onResponse(call: Call<OrderListResponse>, response: Response<OrderListResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.d("CancelledOrdersFragment", "Response: ${response.body()}")

                    val ordersList = response.body()?.orders ?: emptyList()

                    // Check for both "Cancelled" and "Canceled" to avoid mismatches
                    val filteredOrders = ordersList.filter {
                        it.status.equals("Cancelled", ignoreCase = true) ||
                                it.status.equals("Canceled", ignoreCase = true)
                    }

                    // Update RecyclerView
                    cancelledOrders.clear()
                    cancelledOrders.addAll(filteredOrders)
                    orderAdapter.notifyDataSetChanged()

                    // Update UI visibility
                    updateEmptyState()
                } else {
                    Log.e("CancelledOrdersFragment", "Failed: ${response.code()} ${response.errorBody()?.string()}")
                    showEmptyMessage("Failed to load cancelled orders.")
                }
            }

            override fun onFailure(call: Call<OrderListResponse>, t: Throwable) {
                Log.e("CancelledOrdersFragment", "API call failed: ${t.message}", t)
                showEmptyMessage("Failed to load cancelled orders. Please try again later.")
            }
        })
    }

    private fun updateEmptyState() {
        if (cancelledOrders.isEmpty()) {
            showEmptyMessage("No cancelled orders found.")
        } else {
            showOrders()
        }
    }

    private fun showEmptyMessage(message: String) {
        emptyTextView.text = message
        emptyTextView.visibility = View.VISIBLE
        orderRecyclerView.visibility = View.GONE
    }

    private fun showOrders() {
        emptyTextView.visibility = View.GONE
        orderRecyclerView.visibility = View.VISIBLE
    }

    override fun onOrderCancelled(order: OrderModel) {
        // If a user manually cancels an order, remove it from the list
        cancelledOrders.removeAll { it.id == order.id }
        orderAdapter.notifyDataSetChanged()

        // Show empty message if no more cancelled orders exist
        if (cancelledOrders.isEmpty()) {
            showEmptyMessage("No cancelled orders found.")
        }
    }

    // Empty implementation for onOrderCompleted
    override fun onOrderCompleted(order: OrderModel) {
        Log.d("CancelledOrdersFragment", "Order completed: ${order.id}")
    }
}

