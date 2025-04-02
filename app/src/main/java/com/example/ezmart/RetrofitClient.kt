package com.example.ezmart.api

import com.example.ezmart.auth.ResetPasswordRequest
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
import retrofit2.http.Path
import retrofit2.http.Query

// Retrofit Client and API Interface
object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.9/"

    // Create a logging interceptor
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
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
            .create(Service::class.java)
    }

    // Define API endpoints
    interface Service {
        // Login endpoint
        @POST("EZ-WEB/auth/login_mobile.php")
        fun login(@Body request: LoginRequest): Call<LoginResponse>

        // Register endpoint
        @POST("EZ-WEB/auth/register_mobile.php")
        fun register(@Body request: RegisterRequest): Call<RegisterResponse>

        // Fetch products by category
        @GET("EZ-WEB/api/fetch_products.php")
        fun getProductsByCategory(
            @Query("category") category: String
        ): Call<ProductResponse>

        // Search products
        @GET("EZ-WEB/api/search_products.php")
        fun searchProducts(
            @Query("query") query: String
        ): Call<ProductResponse>

        // Place order
        @POST("EZ-WEB/api/place_order.php")
        fun placeOrder(@Body request: OrderRequest): Call<OrderResponse>

        // Get orders
        @GET("EZ-WEB/api/get_orders.php")
        fun getOrders(@Query("user_id") userId: String): Call<OrderListResponse>

        // Forgot password (Send OTP)
        @POST("EZ-WEB/auth/forgot_password.php")
        fun sendOtp(@Body request: Map<String, String>): Call<ResponseBody>

        // Reset password
        @POST("EZ-WEB/auth/reset_password.php")
        fun resetPassword(@Body request: ResetPasswordRequest): Call<ResponseBody>

        // Update user profile
        @FormUrlEncoded
        @POST("EZ-WEB/api/update_profile.php")
        fun updateProfile(
            @Field("email") email: String,
            @Field("first_name") firstName: String,
            @Field("last_name") lastName: String,
            @Field("birthdate") birthdate: String,
            @Field("contact") contact: String,
            @Field("address") address: String,
            @Field("gender") gender: String
        ): Call<ProfileResponse>

        // Get user profile
        @GET("EZ-WEB/api/get_profile.php")
        fun getProfile(@Query("email") email: String): Call<ProfileResponse>

        // Reduce stock when product is bought
        @POST("EZ-WEB/api/productapi.php")
        fun reduceStock(@Body request: StockUpdateRequest): Call<ResponseBody>

        // Update Firebase Cloud Messaging (FCM) token
        @POST("EZ-WEB/api/update_fcm_token.php")
        fun updateFcmToken(@Body request: Map<String, String>): Call<ResponseBody>

        // Process payment via PayMongo
        @POST("EZ-WEB/api/paymongo_status.php")
        fun processPayment(@Body paymentData: PaymentData): Call<PaymentResponse>

        // Cancel order and restore stock
        @FormUrlEncoded
        @POST("EZ-WEB/api/cancel_order.php")
        fun cancelOrder(@Field("order_id") orderId: String): Call<ApiResponse>

        // Get notifications
        @GET("EZ-WEB/api/get_notification.php")
        fun getNotifications(
            @Query("user_id") userId: Int
        ): Call<List<NotificationModel>>

        // Save notification to database
        @FormUrlEncoded
        @POST("EZ-WEB/api/save_notification.php")
        fun saveNotification(
            @Field("user_id") userId: String,
            @Field("message") message: String,
            @Field("status") status: String = "unread"
        ): Call<ResponseBody>

        // Update token
        @POST("EZ-WEB/api/update_fcm_token.php")
        fun updateToken(@Body tokenData: Map<String, String>): Call<ResponseBody>

        @FormUrlEncoded
        @POST("EZ-WEB/api/update_order_status.php")
        fun updateOrderStatus(
            @Field("id") id: String,
            @Field("status") status: String
        ): Call<ApiResponse>
    }
}
