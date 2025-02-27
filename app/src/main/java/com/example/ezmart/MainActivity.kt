package com.example.ezmart

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var cartIbtn: ImageButton
    private lateinit var searchBtn: ImageButton
    private lateinit var sliderHandler: Handler
    private lateinit var sliderRunnable: Runnable
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productList: List<Product>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Buttons
        cartIbtn = findViewById(R.id.cartIbtn)
        cartIbtn.setOnClickListener { navigateToCart() }

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

        // Set default selected tab (Categories)
        homeIcon.setColorFilter(resources.getColor(R.color.blue))
        homeText.setTextColor(resources.getColor(R.color.blue))

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

        // Image Slider Setup
        val imageList = listOf(R.drawable.ad1, R.drawable.ad2, R.drawable.ad3)
        val loopedList = listOf(imageList.last()) + imageList + listOf(imageList.first())

        viewPager = findViewById(R.id.viewPager)
        val tabIndicator: TabLayout = findViewById(R.id.tabIndicator)
        val adapter = ImageSliderAdapter(loopedList)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(1, false)

        TabLayoutMediator(tabIndicator, viewPager) { _, _ -> }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == loopedList.size - 1) {
                    viewPager.post { viewPager.setCurrentItem(1, false) }
                } else if (position == 0) {
                    viewPager.post { viewPager.setCurrentItem(loopedList.size - 2, false) }
                }
            }
        })

        sliderHandler = Handler(Looper.getMainLooper())
        sliderRunnable = object : Runnable {
            override fun run() {
                viewPager.setCurrentItem(viewPager.currentItem + 1, true)
                sliderHandler.postDelayed(this, 2000)
            }
        }
        sliderHandler.postDelayed(sliderRunnable, 2000)

        // RecyclerView Product Listing
        recyclerView = findViewById(R.id.unbeatableRv)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        productList = listOf(
            Product(R.drawable.nissin_wafer, "Nissin Wafer Choco", "₱ 50.00"),
            Product(R.drawable.knorr_sinigang, "Knorr Sinigang Mix", "₱ 15.00"),
        )

        productAdapter = ProductAdapter(this, productList)
        recyclerView.adapter = productAdapter

        recyclerView = findViewById(R.id.featruedRv)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        productList = listOf(
            Product(R.drawable.century_tuna, "Century Tuna", "₱ 30.00"),
            Product(R.drawable.sugar, "Sugar", "₱ 20.00"),
        )

        productAdapter = ProductAdapter(this, productList)
        recyclerView.adapter = productAdapter

        recyclerView = findViewById(R.id.snacksRv)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        productList = listOf(
            Product(R.drawable.skyflakes, "Skyflakes Bundle", "₱ 60.00"),
            Product(R.drawable.cream_o, "Cream O Bundle", "₱ 75.00"),
        )

        productAdapter = ProductAdapter(this, productList)
        recyclerView.adapter = productAdapter

        recyclerView = findViewById(R.id.pantryRv)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        productList = listOf(
            Product(R.drawable.youngstown, "Youngstown Sardines", "₱ 25.00"),
            Product(R.drawable.cornbeef, "Purefoods Cornbeef", "₱ 45.00"),
        )

        productAdapter = ProductAdapter(this, productList)
        recyclerView.adapter = productAdapter
    }

    // Function to navigate to Cart Activity
    private fun navigateToCart() {
        val intent = Intent(this, Cart::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        sliderHandler.removeCallbacks(sliderRunnable)
    }
}
