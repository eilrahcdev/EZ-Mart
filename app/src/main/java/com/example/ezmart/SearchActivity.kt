package com.example.ezmart

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ezmart.api.RetrofitClient
import com.example.ezmart.models.ProductResponse
import com.example.ezmart.utils.UserSession
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEt: EditText
    private lateinit var searchHistoryRecyclerView: RecyclerView
    private lateinit var productRecyclerView: RecyclerView

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var historyAdapter: SearchHistoryAdapter
    private lateinit var productAdapter: ProductAdapter
    private lateinit var userSession: UserSession

    private val searchHistory = mutableListOf<String>()
    private val productList = mutableListOf<Product>()
    private var userEmail: String? = null  // Store user email to manage history properly

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.search_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        searchEt = findViewById(R.id.searchEt)
        searchHistoryRecyclerView = findViewById(R.id.searchHistoryRecyclerView)
        productRecyclerView = findViewById(R.id.productRecyclerView)

        // Initialize UserSession
        userSession = UserSession(this)
        userEmail = userSession.getUser()?.email

        // Ensure user email is available
        if (userEmail == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("SearchPrefs", Context.MODE_PRIVATE)

        loadSearchHistory()

        // Setup RecyclerViews
        searchHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
        historyAdapter = SearchHistoryAdapter(searchHistory) { searchQuery ->
            searchEt.setText(searchQuery)
            performSearch(searchQuery)
        }
        searchHistoryRecyclerView.adapter = historyAdapter

        productRecyclerView.layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter(this, productList)
        productRecyclerView.adapter = productAdapter

        // Search button click listener
        findViewById<ImageButton>(R.id.searchIcon).setOnClickListener {
            val query = searchEt.text.toString().trim()
            if (!TextUtils.isEmpty(query)) {
                saveSearchHistory(query)
                performSearch(query)
            }
        }

        // Back button click listener
        findViewById<ImageButton>(R.id.backBtn_search).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Clear history button click listener
        findViewById<TextView>(R.id.clearHistoryBtn).setOnClickListener {
            clearSearchHistory()
        }
    }

    // Save search history specific to each user
    private fun saveSearchHistory(query: String) {
        if (!searchHistory.contains(query)) {
            searchHistory.add(0, query)
            val historyKey = "history_$userEmail"
            sharedPreferences.edit().putStringSet(historyKey, searchHistory.toSet()).apply()
            historyAdapter.notifyDataSetChanged()
        }
    }

    // Load search history specific to each user
    private fun loadSearchHistory() {
        searchHistory.clear()
        val historyKey = "history_$userEmail"
        searchHistory.addAll(sharedPreferences.getStringSet(historyKey, emptySet())!!.toList())
    }

    // Clear search history for the logged-in user
    private fun clearSearchHistory() {
        searchHistory.clear()
        val historyKey = "history_$userEmail"
        sharedPreferences.edit().remove(historyKey).apply()
        historyAdapter.notifyDataSetChanged()
    }

    // Search products directly from API based on user input
    private fun performSearch(query: String) {
        val apiService = RetrofitClient.instance
        val call = apiService.searchProducts(query)

        call.enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    productList.clear()
                    val filteredProducts = response.body()!!.products
                    if (filteredProducts.isNotEmpty()) {
                        productList.addAll(filteredProducts)
                    } else {
                        Toast.makeText(this@SearchActivity, "No products found", Toast.LENGTH_SHORT).show()
                    }
                    productAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@SearchActivity, "Failed to load products", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                Log.e("SearchActivity", "Error loading products", t)
            }
        })
    }
}
