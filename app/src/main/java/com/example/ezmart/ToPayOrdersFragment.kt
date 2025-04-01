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
import com.example.ezmart.api.RetrofitClient
import com.example.ezmart.models.OrderListResponse
import com.example.ezmart.models.OrderModel
import com.example.ezmart.utils.UserSession
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ToPayOrdersFragment : Fragment(), OrderUpdateListener {

    companion object {
        fun newInstance(): ToPayOrdersFragment {
            return ToPayOrdersFragment()
        }
    }

    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var emptyTextView: TextView
    private var toPayOrders: MutableList<OrderModel> = mutableListOf()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_to_pay_orders, container, false)

        // Initialize Views
        orderRecyclerView = view.findViewById(R.id.recyclerView_orders)
        emptyTextView = view.findViewById(R.id.emptyOrdersTextView)

        orderRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Pass context & OrderUpdateListener to OrderAdapter
        orderAdapter = OrderAdapter(toPayOrders, requireContext(), this)
        orderRecyclerView.adapter = orderAdapter

        // Load To Pay Orders
        loadToPayOrders()

        return view
    }

    override fun onResume() {
        super.onResume()
        loadToPayOrders()
    }

    private fun loadToPayOrders() {
        val userSession = UserSession(requireContext())
        val user = userSession.getUser()

        if (user == null) {
            showEmptyMessage("User is not logged in.")
            return
        }

        RetrofitClient.instance.getOrders(user.id.toString()).enqueue(object : Callback<OrderListResponse> {
            override fun onResponse(call: Call<OrderListResponse>, response: Response<OrderListResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val orderResponse = response.body()!!

                    if (orderResponse.success) {
                        val ordersList = orderResponse.orders
                        val filteredOrders = ordersList.filter { it.status == "To Pay" }

                        // Update RecyclerView
                        toPayOrders.clear()
                        toPayOrders.addAll(filteredOrders)
                        orderAdapter.notifyDataSetChanged()

                        // Show/hide empty message
                        if (toPayOrders.isEmpty()) {
                            showEmptyMessage("No orders to pay.")
                        } else {
                            showOrders()
                        }
                    } else {
                        showEmptyMessage("No orders found.")
                    }
                } else {
                    showEmptyMessage("Failed to load orders.")
                }
            }

            override fun onFailure(call: Call<OrderListResponse>, t: Throwable) {
                showEmptyMessage("Error: ${t.message}")
            }
        })
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
        // Remove the canceled order from the list
        toPayOrders.removeAll { it.id == order.id }
        orderAdapter.notifyDataSetChanged()

        // Show empty message if no more "To Pay" orders
        if (toPayOrders.isEmpty()) {
            showEmptyMessage("No orders to pay.")
        }
    }
}
