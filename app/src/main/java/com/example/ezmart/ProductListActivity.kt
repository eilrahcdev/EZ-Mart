package com.example.ezmart

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ezmart.api.ApiClient
import com.example.ezmart.models.ProductResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewProducts)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        productAdapter = ProductAdapter(this, listOf()) // start with empty list
        recyclerView.adapter = productAdapter

        // Get the category from intent
        val category = intent.getStringExtra("CATEGORY_NAME") ?: ""

        // Fetch products from API using the category
        fetchProducts(category)
    }

    // Now accepts category as parameter
    private fun fetchProducts(category: String) {
        ApiClient.instance.getProductsByCategory(category).enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val products = response.body()?.products ?: emptyList()

                    // Update RecyclerView
                    productAdapter.updateProductList(products)
                } else {
                    Toast.makeText(this@ProductListActivity, "Failed to load products", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                Toast.makeText(this@ProductListActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
