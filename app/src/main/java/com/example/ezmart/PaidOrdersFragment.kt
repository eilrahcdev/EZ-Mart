package com.example.ezmart

import android.annotation.SuppressLint
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

class PaidOrdersFragment : Fragment(), OrderUpdateListener {

    companion object {
        fun newInstance(): PaidOrdersFragment {
            return PaidOrdersFragment()
        }
    }

    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var emptyTextView: TextView
    private var paidOrders: MutableList<OrderModel> = mutableListOf()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_paid_orders, container, false)

        orderRecyclerView = view.findViewById(R.id.recyclerView_orders)
        emptyTextView = view.findViewById(R.id.emptyOrdersTextView)

        orderRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Pass context first, then listener
        orderAdapter = OrderAdapter(paidOrders, requireContext(), this)
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

                    paidOrders.clear()
                    paidOrders.addAll(ordersList.filter { it.status.equals("Paid", ignoreCase = true) })

                    updateUI()
                } else {
                    showErrorMessage("No pending orders found.")
                }
            }

            override fun onFailure(call: Call<OrderListResponse>, t: Throwable) {
                Log.e("PaidOrdersFragment", "Error loading orders: ${t.message}", t)
                showErrorMessage("Error: ${t.message}")
            }
        })
    }

    private fun updateUI() {
        if (paidOrders.isEmpty()) {
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
        paidOrders.remove(order)
        orderAdapter.notifyDataSetChanged()
        updateUI()
    }
}
