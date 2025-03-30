package com.example.ezmart

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.ezmart.api.RetrofitClient
import com.example.ezmart.models.OrderCancelRequest
import com.example.ezmart.models.OrderListResponse
import com.example.ezmart.models.OrderModel
import com.example.ezmart.utils.UserSession
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Orders : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var ordersPagerAdapter: OrdersPagerAdapter
    private lateinit var userSession: UserSession

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.orders_activity)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.orders_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Views
        tabLayout = findViewById(R.id.ordersTabLayout)
        viewPager = findViewById(R.id.ordersViewPager)

        // Initialize UserSession
        userSession = UserSession(this)

        // Check if user is logged in
        val loggedInUser = userSession.getUser()
        if (loggedInUser == null) {
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }

        // Fetch Orders
        fetchOrders(loggedInUser.id)

        // Back Button Functionality
        findViewById<ImageButton>(R.id.backBtn_orders).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Setup Bottom Navigation
        setupNavigation()
    }

    private fun fetchOrders(userId: Int) {
        Log.d("OrdersActivity", "Fetching orders for user ID: $userId")

        RetrofitClient.instance.getOrders(userId.toString()).enqueue(object : Callback<OrderListResponse> {
            override fun onResponse(call: Call<OrderListResponse>, response: Response<OrderListResponse>) {
                if (response.isSuccessful) {
                    val orderResponse = response.body()
                    val orders = orderResponse?.orders ?: emptyList() // Ensure non-null list

                    // Log fetched orders
                    orders.forEach { order ->
                        Log.d("OrdersActivity", "Order ID: ${order.id}, Status: ${order.status}")
                    }

                    Log.d("OrdersActivity", "Fetched ${orders.size} orders")
                    setupViewPager(orders)
                } else {
                    Log.e("OrdersActivity", "Failed: ${response.code()} ${response.errorBody()?.string()}")
                    setupViewPager(emptyList()) // Show empty UI
                }
            }

            override fun onFailure(call: Call<OrderListResponse>, t: Throwable) {
                Log.e("OrdersActivity", "API call failed: ${t.message}", t)
            }
        })
    }


    private fun setupViewPager(orders: List<OrderModel>) {
        val pendingOrders = orders.filter { it.status == "Pending" }
        val processingOrders = orders.filter { it.status == "To Pay" }
        val completedOrders = orders.filter { it.status == "Completed" }
        val cancelledOrders = orders.filter { it.status == "Cancelled" }

        ordersPagerAdapter = OrdersPagerAdapter(this, pendingOrders, processingOrders, completedOrders, cancelledOrders)
        viewPager.adapter = ordersPagerAdapter

        // Set up TabLayout with ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Pending"
                1 -> "To Pay"
                2 -> "Completed"
                3 -> "Cancelled"
                else -> ""
            }
        }.attach()
    }

    private fun setupNavigation() {
        val navigationItems = listOf(
            R.id.homeTab to Pair(R.id.homeIcon, R.id.homeText),
            R.id.categoriesTab to Pair(R.id.categoriesIcon, R.id.categoriesText),
            R.id.ordersTab to Pair(R.id.ordersIcon, R.id.ordersText),
            R.id.profileTab to Pair(R.id.profileIcon, R.id.profileText)
        )

        setDefaultOrdersTab()

        navigationItems.forEach { (tabId, iconTextPair) ->
            val (iconId, textId) = iconTextPair
            findViewById<LinearLayout>(tabId).setOnClickListener {
                if (tabId == R.id.ordersTab) return@setOnClickListener // Prevent restarting the same activity

                resetNavigationColors()
                findViewById<ImageView>(iconId).setColorFilter(
                    ContextCompat.getColor(this, R.color.blue)
                )
                findViewById<TextView>(textId).setTextColor(
                    ContextCompat.getColor(this, R.color.blue)
                )
                startActivity(Intent(this, getDestinationClass(tabId)))
                finish()
            }
        }
    }

    private fun setDefaultOrdersTab() {
        findViewById<ImageView>(R.id.ordersIcon).setColorFilter(
            ContextCompat.getColor(this, R.color.blue)
        )
        findViewById<TextView>(R.id.ordersText).setTextColor(
            ContextCompat.getColor(this, R.color.blue)
        )
    }

    private fun getDestinationClass(tabId: Int): Class<*> {
        return when (tabId) {
            R.id.categoriesTab -> Categories::class.java
            R.id.profileTab -> Profile::class.java
            else -> MainActivity::class.java
        }
    }

    private fun resetNavigationColors() {
        listOf(R.id.homeIcon, R.id.categoriesIcon, R.id.ordersIcon, R.id.profileIcon).forEach {
            findViewById<ImageView>(it).setColorFilter(ContextCompat.getColor(this, R.color.black))
        }
        listOf(R.id.homeText, R.id.categoriesText, R.id.ordersText, R.id.profileText).forEach {
            findViewById<TextView>(it).setTextColor(ContextCompat.getColor(this, R.color.black))
        }
    }
}
