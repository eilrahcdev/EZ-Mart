package com.example.ezmart.api

import com.example.ezmart.models.LoginRequest
import com.example.ezmart.models.LoginResponse
import com.example.ezmart.models.ProductResponse
import com.example.ezmart.models.RegisterRequest
import com.example.ezmart.models.RegisterResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// Retrofit Client and API Interface combined
object ApiClient {
    private const val BASE_URL = "http://192.168.1.9/WEB-SM/"

    // Create a logging interceptor
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Logs request and response body
    }

    // Create an OkHttp client with the logging interceptor
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // Create a Retrofit instance
    val instance: Service by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Base URL for all API calls
            .client(okHttpClient) // Use the OkHttp client
            .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON parsing
            .build()
            .create(Service::class.java) // Create the API service
    }

    // Define API endpoints
    interface Service {
        // Login endpoint
        @POST("auth/login_mobile.php") // ✅ Corrected
        fun login(@Body request: LoginRequest): Call<LoginResponse>

        // Register endpoint
        @POST("auth/register_mobile.php") // ✅ Corrected
        fun register(@Body request: RegisterRequest): Call<RegisterResponse>

        // Fetch products endpoint by category
        @GET("api/productapi.php") // ✅ Fixed path
        fun getProductsByCategory(@Query("category") category: String): Call<ProductResponse>

        // Search products endpoint
        @GET("api/search_products.php") // ✅ Fixed path
        fun searchProducts(@Query("query") query: String): Call<ProductResponse>
    }
}
