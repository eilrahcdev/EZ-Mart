package com.example.ezmart

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
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
import com.example.ezmart.models.OrderModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Orders : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var ordersPagerAdapter: OrdersPagerAdapter

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

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Fetch Orders
        fetchOrders()

        // Back Button Functionality
        findViewById<ImageButton>(R.id.backBtn_orders).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Setup Bottom Navigation
        setupNavigation()
    }

    private fun fetchOrders() {
        val userEmail = sharedPreferences.getString("loggedInUser", null)

        if (userEmail.isNullOrEmpty()) {
            Toast.makeText(this, "Error: User email is missing!", Toast.LENGTH_LONG).show()
            return
        }

        val apiService = RetrofitClient.instance

        apiService.getOrders(userEmail).enqueue(object : Callback<List<OrderModel>> {
            override fun onResponse(call: Call<List<OrderModel>>, response: Response<List<OrderModel>>) {
                if (response.isSuccessful && response.body() != null) {
                    val orders = response.body()!!
                    Log.d("OrdersActivity", "Fetched ${orders.size} orders")

                    setupViewPager(orders)
                } else {
                    Log.e("OrdersActivity", "Failed to fetch orders")
                }
            }

            override fun onFailure(call: Call<List<OrderModel>>, t: Throwable) {
                Log.e("OrdersActivity", "API call failed: ${t.message}")
            }
        })
    }

    private fun setupViewPager(orders: List<OrderModel>) {
        ordersPagerAdapter = OrdersPagerAdapter(this, orders)
        viewPager.adapter = ordersPagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Pending"
                1 -> "Processing"
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
