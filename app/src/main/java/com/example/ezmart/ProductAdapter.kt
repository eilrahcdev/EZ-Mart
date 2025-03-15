package com.example.ezmart

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductAdapter(private val context: Context, private var productList: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.productImage)
        val productName: TextView = view.findViewById(R.id.productName)
        val productPrice: TextView = view.findViewById(R.id.productPrice)
        val addToCartBtn: Button = view.findViewById(R.id.addToCartButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        Glide.with(context)
            .load(product.image)
            .into(holder.productImage)

        holder.productName.text = product.name
        holder.productPrice.text = "₱ %.2f".format(product.price) // Format price properly

        // Handle Add to Cart button
        holder.addToCartBtn.setOnClickListener {
            CartManager.addToCart(context, product)
        }
    }

    override fun getItemCount(): Int = productList.size

    // Function to update the adapter's data and refresh RecyclerView
    fun updateProductList(newProductList: List<Product>) {
        productList = newProductList
        notifyDataSetChanged()
    }


    // Optional: Search products and save search history
    fun searchProducts(query: String, allProducts: List<Product>) {
        val filteredList = allProducts.filter { it.name.contains(query, ignoreCase = true) }
        updateProductList(filteredList)
        SearchHistoryManager.saveSearchQuery(context, query)
    }
}

// Search history manager using SharedPreferences
object SearchHistoryManager {
    private const val PREFS_NAME = "search_history"
    private const val KEY_HISTORY = "history"

    fun saveSearchQuery(context: Context, query: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val historySet = prefs.getStringSet(KEY_HISTORY, mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        if (query.isNotBlank()) {
            historySet.add(query) // Avoid duplicates
            prefs.edit().putStringSet(KEY_HISTORY, historySet).apply()
            Log.d("SearchHistory", "Saved Query: $query")
        }
    }

    fun getSearchHistory(context: Context): List<String> {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_HISTORY, setOf())?.toList() ?: emptyList()
    }
}
