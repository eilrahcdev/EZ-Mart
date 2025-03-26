package com.example.ezmart

import android.annotation.SuppressLint
import android.content.Intent
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
        val totalAmountTextView = findViewById<TextView>(R.id.total_amount)
        totalAmountTextView.text = "Total: ₱ %.2f".format(calculateTotalAmount())

        // Place order button
        val placeOrderButton = findViewById<Button>(R.id.place_order_button)
        placeOrderButton.setOnClickListener {
            placeOrder()
        }

        // Back button functionality
        findViewById<ImageButton>(R.id.backBtn_checkout).setOnClickListener {
            startActivity(Intent(this, Cart::class.java))
            finish()
        }

        // Payment method radio button modifications
        val cashOnPickupRb = findViewById<RadioButton>(R.id.cash_on_pickup)
        val gcash = findViewById<RadioButton>(R.id.gcash)
        val paymaya = findViewById<RadioButton>(R.id.paymaya)

        val radioButtonColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue))
        cashOnPickupRb.buttonTintList = radioButtonColor
        gcash.buttonTintList = radioButtonColor
        paymaya.buttonTintList = radioButtonColor
    }

    private fun calculateTotalAmount(): Double {
        return productList.sumOf { it.price * it.quantity }
    }

    private fun getSelectedProducts(): List<Product> {
        val products = intent.getParcelableArrayListExtra<Product>("selected_products")

        // Debugging log
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

        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("loggedInUser", null)

        if (userEmail.isNullOrEmpty()) {
            Toast.makeText(this, "Error: User email is missing!", Toast.LENGTH_LONG).show()
            return
        }

        val order = OrderModel(
            id = System.currentTimeMillis().toString(),
            email = userEmail,
            customerName = "Customer Name",
            products = productList,
            totalAmount = calculateTotalAmount(),
            status = "Pending",
            paymentMethod = paymentMethod
        )

        saveOrder(order)
        removeCheckedOutProducts()

        Toast.makeText(this, "Order Placed Successfully!", Toast.LENGTH_SHORT).show()

        // Redirect to My Orders page
        startActivity(Intent(this, Orders::class.java))
        finish()
    }

    private fun saveOrder(orderRequest: OrderModel) {
        val apiService = RetrofitClient.instance

        apiService.placeOrder(orderRequest).enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        Log.d("CheckoutActivity", "Order saved successfully for Email: ${orderRequest.email}")

                        removeCheckedOutProducts()
                        Toast.makeText(this@Checkout, "Order Placed Successfully!", Toast.LENGTH_SHORT).show()

                        // Redirect to My Orders page
                        startActivity(Intent(this@Checkout, Orders::class.java))
                        finish()
                    } else {
                        Log.e("CheckoutActivity", "Failed to save order: ${apiResponse?.error ?: "Unknown error"}")
                        Toast.makeText(this@Checkout, "Failed to place order!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("CheckoutActivity", "API Error: ${response.code()} - ${response.errorBody()?.string()}")
                    Toast.makeText(this@Checkout, "API Error: Unable to place order", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                Log.e("CheckoutActivity", "API call failed: ${t.localizedMessage}")
                Toast.makeText(this@Checkout, "Network error. Try again later.", Toast.LENGTH_SHORT).show()
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
