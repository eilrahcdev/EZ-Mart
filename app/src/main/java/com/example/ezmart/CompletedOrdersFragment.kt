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
import com.example.ezmart.models.OrderListResponse
import com.example.ezmart.models.OrderModel
import com.example.ezmart.utils.UserSession
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompletedOrdersFragment : Fragment(), OrderUpdateListener {

    companion object {
        fun newInstance(): CompletedOrdersFragment {
            return CompletedOrdersFragment()
        }
    }

    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var emptyTextView: TextView
    private var completedOrders: MutableList<OrderModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_completed_orders, container, false)

        // Initialize Views
        orderRecyclerView = view.findViewById(R.id.recyclerView_orders)
        emptyTextView = view.findViewById(R.id.emptyOrdersTextView)

        orderRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize Adapter with OrderUpdateListener
        orderAdapter = OrderAdapter(completedOrders, requireContext(), this)
        orderRecyclerView.adapter = orderAdapter

        // Load Completed Orders
        loadCompletedOrders()

        return view
    }

    // Refresh the list on fragment resume
    override fun onResume() {
        super.onResume()
        loadCompletedOrders()
    }

    private fun loadCompletedOrders() {
        val userSession = UserSession(requireContext())
        val user = userSession.getUser()

        if (user == null) {
            showEmptyMessage("Please log in to view orders")
            return
        }

        RetrofitClient.instance.getOrders(user.id.toString()).enqueue(object : Callback<OrderListResponse> {
            override fun onResponse(call: Call<OrderListResponse>, response: Response<OrderListResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.d("CompletedOrdersFragment", "Response: ${response.body()}")

                    val ordersList = response.body()?.orders ?: emptyList()

                    // Filter orders with "Completed" status
                    val filteredOrders = ordersList.filter { it.status.equals("Completed", ignoreCase = true) }

                    // Update RecyclerView
                    completedOrders.clear()
                    completedOrders.addAll(filteredOrders)
                    orderAdapter.notifyDataSetChanged()

                    // Update UI
                    updateEmptyState()
                } else {
                    Log.e("CompletedOrdersFragment", "Failed: ${response.code()} ${response.errorBody()?.string()}")
                    showEmptyMessage("Failed to load completed orders")
                }
            }

            override fun onFailure(call: Call<OrderListResponse>, t: Throwable) {
                Log.e("CompletedOrdersFragment", "API call failed: ${t.message}", t)
                showEmptyMessage("Failed to load completed orders. Please try again later.")
            }
        })
    }

    private fun updateEmptyState() {
        if (completedOrders.isEmpty()) {
            showEmptyMessage("No completed orders found.")
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
        // Remove order from the list dynamically if needed
        completedOrders.removeAll { it.id == order.id }
        orderAdapter.notifyDataSetChanged()

        // Update UI
        if (completedOrders.isEmpty()) {
            showEmptyMessage("No completed orders found.")
        }
    }

    override fun onOrderCompleted(order: OrderModel) {
        Log.d("CompletedOrdersFragment", "Order completed: ${order.id}")
    }
}
