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

class PendingOrdersFragment : Fragment(), OrderUpdateListener {

    companion object {
        fun newInstance(): PendingOrdersFragment {
            return PendingOrdersFragment()
        }
    }

    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var emptyTextView: TextView
    private var pendingOrders: MutableList<OrderModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pending_orders, container, false)

        orderRecyclerView = view.findViewById(R.id.recyclerView_orders)
        emptyTextView = view.findViewById(R.id.emptyOrdersTextView)

        orderRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Pass context first, then listener
        orderAdapter = OrderAdapter(pendingOrders, requireContext(), this)
        orderRecyclerView.adapter = orderAdapter

        loadOrders()

        return view
    }

    override fun onResume() {
        super.onResume()
        loadOrders()
    }

    private fun loadOrders() {
        val userSession = UserSession(requireContext())
        val user = userSession.getUser()

        if (user == null) {
            showErrorMessage("User is not logged in.")
            return
        }

        RetrofitClient.instance.getOrders(user.id.toString()).enqueue(object : Callback<OrderListResponse> {
            override fun onResponse(call: Call<OrderListResponse>, response: Response<OrderListResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val ordersList = response.body()!!.orders

                    pendingOrders.clear()
                    pendingOrders.addAll(ordersList.filter { it.status.equals("Pending", ignoreCase = true) })

                    updateUI()
                } else {
                    showErrorMessage("No pending orders found.")
                }
            }

            override fun onFailure(call: Call<OrderListResponse>, t: Throwable) {
                Log.e("PendingOrdersFragment", "Error loading orders: ${t.message}", t)
                showErrorMessage("Error: ${t.message}")
            }
        })
    }

    private fun updateUI() {
        if (pendingOrders.isEmpty()) {
            orderRecyclerView.visibility = View.GONE
            emptyTextView.visibility = View.VISIBLE
        } else {
            orderRecyclerView.visibility = View.VISIBLE
            emptyTextView.visibility = View.GONE
        }
        orderAdapter.notifyDataSetChanged()
    }

    private fun showErrorMessage(message: String) {
        emptyTextView.text = message
        emptyTextView.visibility = View.VISIBLE
        orderRecyclerView.visibility = View.GONE
    }

    override fun onOrderCancelled(order: OrderModel) {
        pendingOrders.remove(order)
        orderAdapter.notifyDataSetChanged()
        updateUI()
    }
}
