package com.example.ezmart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ezmart.api.ApiClient
import com.example.ezmart.models.ProductResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductViewModel : ViewModel() {

    // LiveData to hold the list of products
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    // LiveData to hold error messages
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    // Fetch products from the API with category (optional)
    fun fetchProducts(category: String? = null) {
        val call: Call<ProductResponse> = if (category.isNullOrEmpty()) {
            // Fetch all products by passing empty category
            ApiClient.instance.getProductsByCategory("")
        } else {
            // Fetch products for a specific category
            ApiClient.instance.getProductsByCategory(category)
        }

        call.enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (productResponse?.success == true) {
                        // Update LiveData with fetched products
                        _products.value = productResponse.products
                    } else {
                        _errorMessage.value = "Failed to fetch products: ${productResponse?.error ?: "Unknown error"}"
                    }
                } else {
                    _errorMessage.value = "Failed to fetch products: ${response.errorBody()?.string() ?: "Unknown error"}"
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                _errorMessage.value = "Network error: ${t.message}"
            }
        })
    }
}