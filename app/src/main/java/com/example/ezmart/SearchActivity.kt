package com.example.ezmart

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
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

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEt: EditText
    private lateinit var searchHistoryRecyclerView: RecyclerView
    private lateinit var productRecyclerView: RecyclerView

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var historyAdapter: SearchHistoryAdapter
    private lateinit var productAdapter: ProductAdapter

    private val searchHistory = mutableListOf<String>()
    private val productList = mutableListOf<Product>()

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
        productAdapter = ProductAdapter(this, productList) // FIXED: Now passing context and correct list
        productRecyclerView.adapter = productAdapter

        // Search button click listener
        val searchBtn = findViewById<ImageButton>(R.id.searchIcon)
        searchBtn.setOnClickListener {
            val query = searchEt.text.toString().trim()
            if (!TextUtils.isEmpty(query)) {
                saveSearchHistory(query)
                performSearch(query)
            }
        }

        // Back button click listener
        val backBtn = findViewById<ImageButton>(R.id.backBtn_search)
        backBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Clear history button click listener
        val clearSearchHistoryBtn = findViewById<TextView>(R.id.clearHistoryBtn)
        clearSearchHistoryBtn.setOnClickListener {
            clearSearchHistory()
        }
    }

    // Save search history
    private fun saveSearchHistory(query: String) {
        if (!searchHistory.contains(query)) {
            searchHistory.add(0, query)
            sharedPreferences.edit().putStringSet("history", searchHistory.toSet()).apply()
            historyAdapter.notifyDataSetChanged()
        }
    }

    // Load search history from SharedPreferences
    private fun loadSearchHistory() {
        searchHistory.clear()
        searchHistory.addAll(sharedPreferences.getStringSet("history", emptySet())!!.toList())
    }

    // Clear search history
    private fun clearSearchHistory() {
        searchHistory.clear()
        sharedPreferences.edit().remove("history").apply()
        historyAdapter.notifyDataSetChanged()
    }

    // Simulate searching for products (updated to use Product objects)
    private fun performSearch(query: String) {
        productList.clear()

        val sampleProducts = listOf(
            Product("Nissin Wafer Choco", 50.00, R.drawable.nissin_wafer),
            Product("Knorr Sinigang Mix", 15.00, R.drawable.knorr_sinigang_original_mix),
            Product( "Sugar", 20.00, R.drawable.sugar),
            Product("Skyflakes Bundle", 60.00, R.drawable.skyflakes),
            Product("Toblerone", 260.00, R.drawable.toblerone),
        )

        // Filter products based on the search query
        val filteredProducts = sampleProducts.filter { it.name.contains(query, ignoreCase = true) }

        if (filteredProducts.isNotEmpty()) {
            productList.addAll(filteredProducts)
        } else {
            // If no product matches, display "No products found"
            Toast.makeText(this, "No products found", Toast.LENGTH_SHORT).show()
        }

        productAdapter.notifyDataSetChanged()
    }
}
