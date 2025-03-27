package com.example.ezmart

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ezmart.api.RetrofitClient
import com.example.ezmart.models.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class Checkout : AppCompatActivity() {

    private lateinit var checkoutRecyclerView: RecyclerView
    private lateinit var checkoutAdapter: CheckoutAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var productList: MutableList<Product> = mutableListOf()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.checkout_activity)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.checkout_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize RecyclerView
        checkoutRecyclerView = findViewById(R.id.checkout_recycler_view)
        checkoutRecyclerView.layoutManager = LinearLayoutManager(this)

        // Get selected products
        productList = getSelectedProducts().toMutableList()

        if (productList.isEmpty()) {
            Toast.makeText(this, "No products selected for checkout.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        checkoutAdapter = CheckoutAdapter(productList)
        checkoutRecyclerView.adapter = checkoutAdapter

        // Update total amount
        updateTotalAmount()

        // Place order button
        findViewById<Button>(R.id.place_order_button).setOnClickListener {
            placeOrder()
        }

        // Back button functionality
        findViewById<ImageButton>(R.id.backBtn_checkout).setOnClickListener {
            startActivity(Intent(this, Cart::class.java))
            finish()
        }

        // Payment method radio button modifications
        setupPaymentMethodRadioButtons()
    }

    private fun setupPaymentMethodRadioButtons() {
        val radioButtonColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue))
        findViewById<RadioButton>(R.id.cash_on_pickup).buttonTintList = radioButtonColor
        findViewById<RadioButton>(R.id.gcash).buttonTintList = radioButtonColor
        findViewById<RadioButton>(R.id.paymaya).buttonTintList = radioButtonColor

        // Set default selection
        findViewById<RadioButton>(R.id.cash_on_pickup).isChecked = true
    }

    private fun updateTotalAmount() {
        findViewById<TextView>(R.id.total_amount).text = "Total: ₱ %.2f".format(calculateTotalAmount())
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
        return when (findViewById<RadioGroup>(R.id.payment_options).checkedRadioButtonId) {
            R.id.cash_on_pickup -> "Cash on Pick-up"
            R.id.gcash -> "Gcash"
            R.id.paymaya -> "Paymaya"
            else -> "Unknown"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun placeOrder() {
        val paymentMethod = getSelectedPaymentMethod()
        val userEmail = sharedPreferences.getString("loggedInUser", null) ?: run {
            Toast.makeText(this, "Error: User email is missing!", Toast.LENGTH_LONG).show()
            return
        }

        val order = OrderModel(
            id = System.currentTimeMillis().toString(),
            email = userEmail,
            customerName = sharedPreferences.getString("userName", "Customer") ?: "Customer",
            products = productList,
            totalAmount = calculateTotalAmount(),
            status = "Pending",
            paymentMethod = paymentMethod,
            action = "place"
        )

        saveOrder(order)
    }

    private fun saveOrder(order: OrderModel) {
        RetrofitClient.instance.placeOrder(order).enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        if (apiResponse.success) {
                            // Save order locally with explicit status
                            val savedOrder = order.copy(status = "Pending")
                            OrderManager.saveOrders(this@Checkout, listOf(savedOrder))

                            // Update UI
                            runOnUiThread {
                                Toast.makeText(this@Checkout, "Order Placed Successfully!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@Checkout, Orders::class.java))
                                finish()
                            }
                        } else {
                            handleOrderError(apiResponse.error ?: "Unknown error")
                        }
                    } ?: handleOrderError("Empty response body")
                } else {
                    handleOrderError("API Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                handleOrderError("Network error: ${t.localizedMessage}")
            }
        })
    }

    private fun handleSuccessfulOrder(order: OrderModel) {
        Log.d("CheckoutActivity", "Order saved successfully for Email: ${order.email}")
        removeCheckedOutProducts()
        Toast.makeText(this@Checkout, "Order Placed Successfully!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this@Checkout, Orders::class.java))
        finish()
    }

    private fun handleOrderError(error: String) {
        Log.e("CheckoutActivity", "Failed to save order: $error")
        Toast.makeText(
            this@Checkout,
            "Failed to place order: $error",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun removeCheckedOutProducts() {
        val cartList = CartManager.getCart(this).toMutableList()
        cartList.removeAll { cartItem ->
            productList.any { it.name == cartItem.name }
        }
        CartManager.clearCart(this)
        CartManager.saveCart(this, cartList)
    }
}