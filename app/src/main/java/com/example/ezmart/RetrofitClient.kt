package com.example.ezmart.api

import com.example.ezmart.models.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// Retrofit Client and API Interface combined
object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.9/"

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
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Service::class.java) // Create the API service
    }

    // Define API endpoints
    interface Service {
        // Login endpoint
        @POST("WEB-SM/auth/login_mobile.php")
        fun login(@Body request: LoginRequest): Call<LoginResponse>

        // Register endpoint
        @POST("WEB-SM/auth/register_mobile.php")
        fun register(@Body request: RegisterRequest): Call<RegisterResponse>

        // Fetch products endpoint by category
        @GET("WEB-SM/api/fetch_products.php")
        fun getProductsByCategory(
            @Query("category") category: String
        ): Call<ProductResponse>

        // Search products endpoint
        @GET("WEB-SM/api/search_products.php")
        fun searchProducts(
            @Query("query") query: String
        ): Call<ProductResponse>

        @POST("WEB-SM/api/place_order.php")
        fun placeOrder(@Body request: OrderModel): Call<OrderResponse>

        // Updated getOrders to use email instead of user_id
        @GET("get_orders.php")
        fun getOrders(@Query("email") email: String): Call<List<OrderModel>>

        @POST("WEB-SM/auth/forgot_password.php")
        fun requestPasswordReset(@Body request: Map<String, String>): Call<ResponseBody>

        @FormUrlEncoded
        @POST("WEB-SM/api/update_profile.php")
        fun updateProfile(
            @Field("email") email: String,
            @Field("first_name") firstName: String,
            @Field("last_name") lastName: String,
            @Field("birthdate") birthdate: String,
            @Field("contact") contact: String,
            @Field("address") address: String,
            @Field("gender") gender: String
        ): Call<ProfileResponse>

        // Reduce stock for multiple products
        @POST("WEB-SM/api/productapi.php")
        fun reduceStock(@Body request: StockUpdateRequest): Call<ResponseBody>

        @POST("WEB-SM/api/update_fcm_token.php")
        fun updateFcmToken(@Body request: Map<String, String>): Call<ResponseBody>
    }
}
