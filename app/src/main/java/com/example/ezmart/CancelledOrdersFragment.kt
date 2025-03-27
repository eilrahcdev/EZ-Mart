package com.example.ezmart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ezmart.models.OrderModel

class CancelledOrdersFragment : Fragment() {

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

        // Initialize Adapter with empty list
        orderAdapter = OrderAdapter(cancelledOrders)
        orderRecyclerView.adapter = orderAdapter

        // Load Cancelled Orders
        loadCancelledOrders()

        return view
    }

    override fun onResume() {
        super.onResume()
        loadCancelledOrders()
    }

    private fun loadCancelledOrders() {
        // Get only the logged-in user's orders
        val ordersList = OrderManager.getOrders(requireContext())

        // Filter orders with status "Cancelled"
        val filteredOrders = ordersList.filter { it.status == "Cancelled" }

        // Update RecyclerView
        cancelledOrders.clear()
        cancelledOrders.addAll(filteredOrders)
        orderAdapter.notifyDataSetChanged()

        // Show/hide empty message
        if (cancelledOrders.isEmpty()) {
            orderRecyclerView.visibility = View.GONE
            emptyTextView.visibility = View.VISIBLE
        } else {
            orderRecyclerView.visibility = View.VISIBLE
            emptyTextView.visibility = View.GONE
        }
    }
}