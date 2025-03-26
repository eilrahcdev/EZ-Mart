package com.example.ezmart

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ezmart.api.RetrofitClient
import com.example.ezmart.models.ProductResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class Sweets : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private var productList: MutableList<Product> = mutableListOf()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.sweets_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.sweets_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.sweetsRv)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        productAdapter = ProductAdapter(this, productList)
        recyclerView.adapter = productAdapter

        // Fetch only Sweets category directly from API
        fetchSweetsProducts()

        // Navigation and Button setup
        setupNavigation()

        // Back button
        findViewById<ImageButton>(R.id.backBtn_sweets).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Cart button
        findViewById<ImageButton>(R.id.cartIbtn_sweets).setOnClickListener {
            startActivity(Intent(this, Cart::class.java))
            finish()
        }
    }

    // Fetch Sweets category products from API
    private fun fetchSweetsProducts() {
        val apiService = RetrofitClient.instance
        val call = apiService.getProductsByCategory("Sweets")

        call.enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    productList.clear()
                    productList.addAll(response.body()!!.products)
                    productAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@Sweets, "Failed to load products", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                Toast.makeText(this@Sweets, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Navigation bar setup function
    private fun setupNavigation() {
        val homeTab = findViewById<LinearLayout>(R.id.homeTab)
        val categoriesTab = findViewById<LinearLayout>(R.id.categoriesTab)
        val ordersTab = findViewById<LinearLayout>(R.id.ordersTab)
        val profileTab = findViewById<LinearLayout>(R.id.profileTab)

        val homeIcon = findViewById<ImageView>(R.id.homeIcon)
        val categoriesIcon = findViewById<ImageView>(R.id.categoriesIcon)
        val ordersIcon = findViewById<ImageView>(R.id.ordersIcon)
        val profileIcon = findViewById<ImageView>(R.id.profileIcon)

        val homeText = findViewById<TextView>(R.id.homeText)
        val categoriesText = findViewById<TextView>(R.id.categoriesText)
        val ordersText = findViewById<TextView>(R.id.ordersText)
        val profileText = findViewById<TextView>(R.id.profileText)

        // Reset function
        fun resetColors() {
            val defaultColor = resources.getColor(R.color.black)
            homeIcon.setColorFilter(defaultColor)
            categoriesIcon.setColorFilter(defaultColor)
            ordersIcon.setColorFilter(defaultColor)
            profileIcon.setColorFilter(defaultColor)

            homeText.setTextColor(defaultColor)
            categoriesText.setTextColor(defaultColor)
            ordersText.setTextColor(defaultColor)
            profileText.setTextColor(defaultColor)
        }

        // Navigation listeners
        homeTab.setOnClickListener {
            resetColors()
            homeIcon.setColorFilter(resources.getColor(R.color.blue))
            homeText.setTextColor(resources.getColor(R.color.blue))
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        categoriesTab.setOnClickListener {
            resetColors()
            categoriesIcon.setColorFilter(resources.getColor(R.color.blue))
            categoriesText.setTextColor(resources.getColor(R.color.blue))
            startActivity(Intent(this, Categories::class.java))
            finish()
        }

        ordersTab.setOnClickListener {
            resetColors()
            ordersIcon.setColorFilter(resources.getColor(R.color.blue))
            ordersText.setTextColor(resources.getColor(R.color.blue))
            startActivity(Intent(this, Orders::class.java))
            finish()
        }

        profileTab.setOnClickListener {
            resetColors()
            profileIcon.setColorFilter(resources.getColor(R.color.blue))
            profileText.setTextColor(resources.getColor(R.color.blue))
            startActivity(Intent(this, Profile::class.java))
            finish()
        }
    }
}
