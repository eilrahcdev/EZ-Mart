package com.example.ezmart

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

@Suppress("DEPRECATION")
class FreshProduce : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productList: List<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.freshproduce_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.freshproduce_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Buttons with function
        val backBtnFreshProduce = findViewById<ImageButton>(R.id.backBtn_freshproduce)
        backBtnFreshProduce.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val cartIbtnFreshProduce = findViewById<ImageButton>(R.id.cartIbtn_freshproduce)
        cartIbtnFreshProduce.setOnClickListener {
            val intent = Intent(this, Cart::class.java)
            startActivity(intent)
            finish()
        }

        //Fresh Produce
        recyclerView = findViewById(R.id.freshproduceRv)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        productList = listOf(
            Product("Eggplant", "₱ 25.00", R.drawable.eggplant),
            Product( "Okra", "₱ 15.00", R.drawable.okra),
            Product( "Squash", "₱ 35.00", R.drawable.squash),
        )
        productAdapter = ProductAdapter(this, productList)
        recyclerView.adapter = productAdapter

        // Navigation Items
        val homeTab = findViewById<LinearLayout>(R.id.homeTab)
        val categoriesTab = findViewById<LinearLayout>(R.id.categoriesTab)
        val ordersTab = findViewById<LinearLayout>(R.id.ordersTab)
        val profileTab = findViewById<LinearLayout>(R.id.profileTab)

        val homeIcon = findViewById<ImageView>(R.id.homeIcon)
        val categoriesIcon = findViewById<ImageView>(R.id.categoriesIcon)
        val ordersIcon = findViewById<ImageView>(R.id.ordersIcon)
        val profileIcon = findViewById<ImageView>(R.id.profileIcon)

        val homeText = findViewById<TextView>(R.id.homeText)
        val categoriesText = findViewById<TextView>(R.id.categoriesText)
        val ordersText = findViewById<TextView>(R.id.ordersText)
        val profileText = findViewById<TextView>(R.id.profileText)

        fun resetColors() {
            homeIcon.setColorFilter(resources.getColor(R.color.black))
            categoriesIcon.setColorFilter(resources.getColor(R.color.black))
            ordersIcon.setColorFilter(resources.getColor(R.color.black))
            profileIcon.setColorFilter(resources.getColor(R.color.black))

            homeText.setTextColor(resources.getColor(R.color.black))
            categoriesText.setTextColor(resources.getColor(R.color.black))
            ordersText.setTextColor(resources.getColor(R.color.black))
            profileText.setTextColor(resources.getColor(R.color.black))
        }

        homeTab.setOnClickListener {
            resetColors()
            homeIcon.setColorFilter(resources.getColor(R.color.blue))
            homeText.setTextColor(resources.getColor(R.color.blue))
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        categoriesTab.setOnClickListener {
            resetColors()
            categoriesIcon.setColorFilter(resources.getColor(R.color.blue))
            categoriesText.setTextColor(resources.getColor(R.color.blue))
            startActivity(Intent(this, Categories::class.java))
            finish()
        }

        ordersTab.setOnClickListener {
            resetColors()
            ordersIcon.setColorFilter(resources.getColor(R.color.blue))
            ordersText.setTextColor(resources.getColor(R.color.blue))
            startActivity(Intent(this, Orders::class.java))
            finish()
        }

        profileTab.setOnClickListener {
            resetColors()
            profileIcon.setColorFilter(resources.getColor(R.color.blue))
            profileText.setTextColor(resources.getColor(R.color.blue))
            startActivity(Intent(this, Profile::class.java))
            finish()
        }
    }
}