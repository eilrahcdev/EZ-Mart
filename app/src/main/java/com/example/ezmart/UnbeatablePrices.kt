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
class UnbeatablePrices : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private var productList: MutableList<Product> = mutableListOf()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.unbeatableprices_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.unbeatableprices_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup RecyclerView
        recyclerView = findViewById(R.id.unbeatableRv)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        productAdapter = ProductAdapter(this, productList)
        recyclerView.adapter = productAdapter

        // Fetch products via Retrofit
        fetchProducts()

        // Setup Buttons
        setupNavigationButtons()

        // Setup Bottom Navigation
        setupBottomNavigation()
    }

    // Fetch products using Retrofit
    private fun fetchProducts() {
        val apiService = RetrofitClient.instance

        // Correct return type: Call<ProductResponse>
        val call = apiService.getProductsByCategory("Unbeatable Prices")

        // Correct callback type: Callback<ProductResponse>
        call.enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    productList.clear()
                    // Access products from response
                    productList.addAll(response.body()!!.products)
                    productAdapter.notifyDataSetChanged()
                } else {
                    Log.e("API Error", "Response not successful: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                Log.e("API Error", "Network error: ${t.message}")
            }
        })
    }


    // Back and Cart buttons
    private fun setupNavigationButtons() {
        val backBtnUnbeatable = findViewById<ImageButton>(R.id.backBtn_unbeatable)
        backBtnUnbeatable.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val cartIbtnUnbeatable = findViewById<ImageButton>(R.id.cartIbtn_unbeatable)
        cartIbtnUnbeatable.setOnClickListener {
            startActivity(Intent(this, Cart::class.java))
            finish()
        }
    }

    // Bottom Navigation Setup
    private fun setupBottomNavigation() {
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

        fun resetColors() {
            val colorDefault = resources.getColor(R.color.black)
            homeIcon.setColorFilter(colorDefault)
            categoriesIcon.setColorFilter(colorDefault)
            ordersIcon.setColorFilter(colorDefault)
            profileIcon.setColorFilter(colorDefault)

            homeText.setTextColor(colorDefault)
            categoriesText.setTextColor(colorDefault)
            ordersText.setTextColor(colorDefault)
            profileText.setTextColor(colorDefault)
        }

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
