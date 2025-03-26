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

class PendingOrdersFragment : Fragment() {

    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var emptyTextView: TextView
    private var pendingOrders: MutableList<OrderModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_pending_orders, container, false)

        // Initialize Views
        orderRecyclerView = view.findViewById(R.id.recyclerView_orders)
        emptyTextView = view.findViewById(R.id.emptyOrdersTextView)

        orderRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize Adapter
        orderAdapter = OrderAdapter(pendingOrders)
        orderRecyclerView.adapter = orderAdapter

        // Load pending orders
        loadOrders()

        return view
    }

    override fun onResume() {
        super.onResume()
        loadOrders()
    }

    private fun loadOrders() {
        // Retrieve orders from OrderManager
        val ordersList = OrderManager.getOrders(requireContext())

        // Filter only pending orders
        pendingOrders.clear()
        pendingOrders.addAll(ordersList.filter { it.status == "Pending" })

        // Show/Hide empty state text
        if (pendingOrders.isEmpty()) {
            orderRecyclerView.visibility = View.GONE
            emptyTextView.visibility = View.VISIBLE
        } else {
            orderRecyclerView.visibility = View.VISIBLE
            emptyTextView.visibility = View.GONE
        }

        orderAdapter.notifyDataSetChanged()
    }
}