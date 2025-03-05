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

        val backButton = findViewById<ImageButton>(R.id.backBtn_cart)
        backButton.setOnClickListener{
            val Intent = Intent(this, MainActivity::class.java)
            startActivity(Intent)
            finish()
        }

        recyclerView = findViewById(R.id.cartRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val cartItems = CartManager.getCart(this).toMutableList()
        cartAdapter = CartAdapter(cartItems)
        recyclerView.adapter = cartAdapter

        checkoutBtn = findViewById(R.id.btnCheckout)
        deleteBtn = findViewById(R.id.Cart_btnDel)
        selectAllCheckBox = findViewById(R.id.checkBox)
        selectAllCheckBox.buttonTintList = ContextCompat.getColorStateList(this, R.color.blue)


        checkoutBtn.setOnClickListener {
            if (cartAdapter.itemCount == 0) {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Proceeding to Checkout", Toast.LENGTH_SHORT).show()
            }
        }

        deleteBtn.setOnClickListener {
            cartAdapter.clearCart(this@Cart)
            Toast.makeText(this, "Cart emptied", Toast.LENGTH_SHORT).show()
        }

        selectAllCheckBox.setOnCheckedChangeListener { _, isChecked ->
            cartAdapter.selectAllItems(isChecked)
        }
    }
}
