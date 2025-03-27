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
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://192.168.100.15/"
    private const val CONNECTION_TIMEOUT = 30L // 30 seconds timeout

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    interface ApiService {
        // Authentication Endpoints
        @POST("EZ-MART/auth/login_mobile.php")
        fun login(@Body request: LoginRequest): Call<LoginResponse>

        @POST("EZ-MART/auth/register_mobile.php")
        fun register(@Body request: RegisterRequest): Call<RegisterResponse>

        @POST("EZ-MART/auth/forgot_password.php")
        fun sendOtp(@Body request: Map<String, String>): Call<ResponseBody>

        @POST("EZ-MART/auth/reset_password.php")
        fun resetPassword(@Body request: ResetPasswordRequest): Call<ResponseBody>

        // Product Endpoints
        @GET("EZ-MART/api/fetch_products.php")
        fun getProductsByCategory(
            @Query("category") category: String
        ): Call<ProductResponse>

        @GET("EZ-MART/api/search_products.php")
        fun searchProducts(
            @Query("query") query: String
        ): Call<ProductResponse>

        // Order Endpoints
        @POST("EZ-MART/api/place_order.php")
        @Headers("Content-Type: application/json")
        fun placeOrder(@Body order: OrderModel): Call<OrderResponse>

        @GET("EZ-MART/api/get_orders.php")
        fun getOrders(@Query("email") email: String): Call<List<OrderModel>>

        @POST("EZ-MART/api/update_order_status.php")
        fun updateOrderStatus(@Body request: UpdateStatusRequest): Call<OrderResponse>

        // Profile Endpoints
        @FormUrlEncoded
        @POST("EZ-MART/api/update_profile.php")
        fun updateProfile(
            @Field("email") email: String,
            @Field("first_name") firstName: String,
            @Field("last_name") lastName: String,
            @Field("birthdate") birthdate: String,
            @Field("contact") contact: String,
            @Field("address") address: String,
            @Field("gender") gender: String
        ): Call<ProfileResponse>

        @POST("EZ-MART/api/update_fcm_token.php")
        fun updateFcmToken(@Body request: Map<String, String>): Call<ResponseBody>

        // Inventory Endpoints
        @POST("EZ-MART/api/productapi.php")
        fun reduceStock(@Body request: StockUpdateRequest): Call<ResponseBody>
    }
}

