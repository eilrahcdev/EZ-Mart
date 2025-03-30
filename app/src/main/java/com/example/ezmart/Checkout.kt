package com.example.ezmart

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ezmart.api.ProductStock
import com.example.ezmart.api.RetrofitClient
import com.example.ezmart.api.StockUpdateRequest
import com.example.ezmart.models.OrderItem
import com.example.ezmart.models.OrderRequest
import com.example.ezmart.models.OrderResponse
import com.example.ezmart.utils.UserSession
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class Checkout : AppCompatActivity() {

    private lateinit var checkoutRecyclerView: RecyclerView
    private lateinit var checkoutAdapter: CheckoutAdapter
    private var productList: MutableList<Product> = mutableListOf()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.checkout_activity)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.checkout_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkoutRecyclerView = findViewById(R.id.checkout_recycler_view)
        checkoutRecyclerView.layoutManager = LinearLayoutManager(this)

        productList = getSelectedProducts().toMutableList()

        if (productList.isEmpty()) {
            Toast.makeText(this, "No products selected for checkout.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        checkoutAdapter = CheckoutAdapter(productList)
        checkoutRecyclerView.adapter = checkoutAdapter

        val totalAmountTextView = findViewById<TextView>(R.id.total_amount)
        totalAmountTextView.text = "Total: ₱ %.2f".format(calculateTotalAmount())

        findViewById<Button>(R.id.place_order_button).setOnClickListener { placeOrder() }
        findViewById<ImageButton>(R.id.backBtn_checkout).setOnClickListener {
            startActivity(Intent(this, Cart::class.java))
            finish()
        }

        val userSession = UserSession(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                userSession.saveFcmToken(token) // Save the token to the session
            } else {
                Log.e("UserSession", "Failed to get FCM token", task.exception)
            }
        }

        val radioButtonColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue))
        findViewById<RadioButton>(R.id.cash_on_pickup).buttonTintList = radioButtonColor
        findViewById<RadioButton>(R.id.gcash).buttonTintList = radioButtonColor
        findViewById<RadioButton>(R.id.paymaya).buttonTintList = radioButtonColor
    }

    private fun calculateTotalAmount(): Double {
        return productList.sumOf { it.price * it.quantity }
    }

    private fun getSelectedProducts(): List<Product> {
        val products = intent.getParcelableArrayListExtra<Product>("selected_products")
        Log.d("CheckoutActivity", "Received products: ${products?.size ?: 0}")

        if (products.isNullOrEmpty()) {
            Toast.makeText(this, "Error: No products received!", Toast.LENGTH_LONG).show()
        }
        return products ?: emptyList()
    }

    private fun getSelectedPaymentMethod(): String {
        val paymentOptions = findViewById<RadioGroup>(R.id.payment_options)
        return when (paymentOptions.checkedRadioButtonId) {
            R.id.cash_on_pickup -> "Cash on Pick-up"
            R.id.gcash -> "Gcash"
            R.id.paymaya -> "Paymaya"
            else -> "Unknown"
        }
    }

    private fun placeOrder() {
        val paymentMethod = getSelectedPaymentMethod()
        val userSession = UserSession(this)
        val user = userSession.getUser()

        if (user == null) {
            Toast.makeText(this, "Error: User session is missing!", Toast.LENGTH_LONG).show()
            return
        }

        if (user.id <= 0) {
            Toast.makeText(this, "Error: User ID is missing or invalid!", Toast.LENGTH_LONG).show()
            return
        }

        if (productList.isNullOrEmpty()) {
            Toast.makeText(this, "Error: No products selected!", Toast.LENGTH_LONG).show()
            return
        }

        val orderRequest = OrderRequest(
            action = "place",
            userId = user.id,
            customerName = "${user.first_name} ${user.last_name}",
            totalPrice = calculateTotalAmount(),
            status = "Pending",
            paymentMethod = paymentMethod,
            fcmToken = user.fcmToken,
            items = productList.map { product ->
                OrderItem(
                    productId = product.id,
                    name = product.name,
                    price = product.price,
                    quantity = product.quantity
                )
            }
        )

        saveOrder(orderRequest)
    }

    private fun saveOrder(order: OrderRequest) {
        RetrofitClient.instance.placeOrder(order).enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        Log.d("CheckoutActivity", "Order saved successfully for User ID: ${order.userId}")

                        // Reduce stock only after successful order placement
                        reduceStockAfterOrder(order)

                        removeCheckedOutProducts()
                        Toast.makeText(applicationContext, "Order Placed Successfully!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(applicationContext, Orders::class.java))
                        finish()
                    } else {
                        Log.e("CheckoutActivity", "Failed to save order: ${apiResponse?.message ?: "Unknown error"}")
                    }
                } else {
                    Log.e("CheckoutActivity", "Failed to place order: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                Log.e("CheckoutActivity", "Error: ${t.message}")
            }
        })
    }

    private fun reduceStockAfterOrder(order: OrderRequest) {
        val stockUpdateRequest = StockUpdateRequest(
            action = "reduce_stock",
            products = order.items.map { OrderItem ->
                ProductStock(OrderItem.productId, OrderItem.quantity)
            }
        )

        RetrofitClient.instance.reduceStock(stockUpdateRequest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!response.isSuccessful) {
                    Log.e("CheckoutActivity", "Stock update failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("CheckoutActivity", "Stock update error: ${t.message}")
            }
        })
    }

    private fun removeCheckedOutProducts() {
        val cartList = CartManager.getCart(this).toMutableList()
        cartList.removeAll { cartItem -> productList.any { it.name == cartItem.name } }
        CartManager.clearCart(this)
        CartManager.saveCart(this, cartList)
    }
}
