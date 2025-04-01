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
    private var cartItems: MutableList<Product> = mutableListOf()

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
        totalAmountTextView = findViewById(R.id.tvTotalAmount)

        selectAllCheckBox.buttonTintList = ContextCompat.getColorStateList(this, R.color.blue)

        // Handle Back Button
        findViewById<ImageButton>(R.id.backBtn_cart).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        cartItems = CartManager.getCart(this).toMutableList()
        cartAdapter = CartAdapter(cartItems) { totalAmount ->
            updateTotalAmount(totalAmount)
        }
        recyclerView.adapter = cartAdapter

        // Checkout Button Click - Pass Selected Products
        checkoutBtn.setOnClickListener {
            val selectedProducts = cartItems.filter { it.isSelected }
            if (selectedProducts.isEmpty()) {
                Toast.makeText(this, "Please select items to checkout!", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, Checkout::class.java)
                intent.putParcelableArrayListExtra("selected_products", ArrayList(selectedProducts))
                startActivity(intent)
                finish()
            }
        }

        // Delete Button Click - Remove Selected Items
        deleteBtn.setOnClickListener {
            val selectedItems = cartItems.filter { it.isSelected }

            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "Please select items to delete.", Toast.LENGTH_SHORT).show()
            } else {
                cartItems.removeAll(selectedItems)
                CartManager.updateCart(this, cartItems) // Save updated cart data
                cartAdapter.notifyDataSetChanged()
                updateTotalAmount(cartAdapter.getTotalAmount())

                Toast.makeText(this, "Selected products removed", Toast.LENGTH_SHORT).show()
            }
        }

        // Select All Checkbox - Toggle Selection
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