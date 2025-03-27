package com.example.ezmart

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ezmart.models.OrderModel

class ProcessingOrdersFragment : Fragment() {

    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var emptyTextView: TextView
    private var processingOrders: MutableList<OrderModel> = mutableListOf()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_processing_orders, container, false)

        // Initialize Views
        orderRecyclerView = view.findViewById(R.id.recyclerView_orders)
        emptyTextView = view.findViewById(R.id.emptyOrdersTextView)

        orderRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize Adapter with empty list
        orderAdapter = OrderAdapter(processingOrders)
        orderRecyclerView.adapter = orderAdapter

        // Load Processing Orders
        loadProcessingOrders()

        return view
    }

    override fun onResume() {
        super.onResume()
        loadProcessingOrders()
    }

    private fun loadProcessingOrders() {
        // Get only the logged-in user's orders
        val ordersList = OrderManager.getOrders(requireContext())

        // Filter orders with status "Processing"
        val filteredOrders = ordersList.filter { it.status == "Processing" }

        // Update RecyclerView
        processingOrders.clear()
        processingOrders.addAll(filteredOrders)
        orderAdapter.notifyDataSetChanged()

        // Show/hide empty message
        if (processingOrders.isEmpty()) {
            orderRecyclerView.visibility = View.GONE
            emptyTextView.visibility = View.VISIBLE
        } else {
            orderRecyclerView.visibility = View.VISIBLE
            emptyTextView.visibility = View.GONE
        }
    }
}
