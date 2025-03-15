package com.example.ezmart

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Cart : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var checkoutBtn: Button
    private lateinit var deleteBtn: ImageButton
    private lateinit var selectAllCheckBox: CheckBox
    private lateinit var totalAmountTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.cart_activity)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cart_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI Elements
        recyclerView = findViewById(R.id.cartRecyclerView)
        checkoutBtn = findViewById(R.id.btnCheckout)
        deleteBtn = findViewById(R.id.Cart_btnDel)
        selectAllCheckBox = findViewById(R.id.checkBox)
        totalAmountTextView = findViewById(R.id.tvTotalAmount) // Make sure this TextView exists in your layout

        selectAllCheckBox.buttonTintList = ContextCompat.getColorStateList(this, R.color.blue)

        // Handle Back Button
        val backButton = findViewById<ImageButton>(R.id.backBtn_cart)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        val cartItems = CartManager.getCart(this).toMutableList()
        cartAdapter = CartAdapter(cartItems) { totalAmount ->
            updateTotalAmount(totalAmount)
        }
        recyclerView.adapter = cartAdapter

        // Checkout Button Click
        checkoutBtn.setOnClickListener {
            if (cartAdapter.itemCount == 0) {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Proceeding to Checkout", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Checkout::class.java)
                startActivity(intent)
                finish()
            }
        }

        // Delete Button Click
        deleteBtn.setOnClickListener {
            cartAdapter.clearCart(this)
            Toast.makeText(this, "Cart emptied", Toast.LENGTH_SHORT).show()
            updateTotalAmount(0.0) // Reset total amount
        }

        // Select All Checkbox
        selectAllCheckBox.setOnCheckedChangeListener { _, isChecked ->
            cartAdapter.selectAllItems(isChecked)
            updateTotalAmount(cartAdapter.getTotalAmount())
        }

        // Initialize total amount display
        updateTotalAmount(cartAdapter.getTotalAmount())
    }

    @SuppressLint("SetTextI18n")
    private fun updateTotalAmount(totalAmount: Double) {
        totalAmountTextView.text = "₱ %.2f".format(totalAmount)
    }
}
