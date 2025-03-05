package com.example.ezmart

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
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
    private lateinit var searchEt: EditText
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
        cartIbtn.setOnClickListener {
            val intent = Intent(this, Cart::class.java)
            startActivity(intent)
            finish()
        }

        searchEt = findViewById(R.id.searchEt)
        searchEt.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
            finish()
        }

        //See more buttons with functions
        val unbeatableseemoreBtn = findViewById<Button>(R.id.unbeatable_seemoreBtn)
        unbeatableseemoreBtn.setOnClickListener {
            val intent = Intent(this, UnbeatablePrices::class.java)
            startActivity(intent)
            finish()
        }

        val featuredseemoreBtn = findViewById<Button>(R.id.featured_seemoreBtn)
        featuredseemoreBtn.setOnClickListener {
            val intent = Intent(this, FeaturedProducts::class.java)
            startActivity(intent)
            finish()
        }

        val snacksseemoreBtn = findViewById<Button>(R.id.snacks_seemoreBtn)
        snacksseemoreBtn.setOnClickListener {
            val intent = Intent(this, Snacks::class.java)
            startActivity(intent)
            finish()
        }
        val sweetssseemoreBtn = findViewById<Button>(R.id.sweets_seemoreBtn)
        sweetssseemoreBtn.setOnClickListener {
            val intent = Intent(this, Sweets::class.java)
            startActivity(intent)
            finish()
        }

        val pantryseemoreBtn = findViewById<Button>(R.id.pantry_seemoreBtn)
        pantryseemoreBtn.setOnClickListener {
            val intent = Intent(this, Pantry::class.java)
            startActivity(intent)
            finish()
        }
        val freshproduceseemoreBtn = findViewById<Button>(R.id.freshproduce_seemoreBtn)
        freshproduceseemoreBtn.setOnClickListener {
            val intent = Intent(this, FreshProduce::class.java)
            startActivity(intent)
            finish()
        }
        val meatsseemoreBtn = findViewById<Button>(R.id.meatsandseafoods_seemoreBtn)
        meatsseemoreBtn.setOnClickListener {
            val intent = Intent(this, MeatsandSeafoods::class.java)
            startActivity(intent)
            finish()
        }
        val householdseemoreBtn = findViewById<Button>(R.id.householdessentials_seemoreBtn)
        householdseemoreBtn.setOnClickListener {
            val intent = Intent(this, HouseholdEssentials::class.java)
            startActivity(intent)
            finish()
        }


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

        //Unbeatable Prices
        recyclerView = findViewById(R.id.unbeatableRv)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        productList = listOf(
            Product("Nissin Wafer Choco", "₱ 50.00", R.drawable.nissin_wafer),
            Product("Knorr Sinigang Mix", "₱ 15.00", R.drawable.knorr_sinigang_original_mix),
        )
        productAdapter = ProductAdapter(this, productList)
        recyclerView.adapter = productAdapter

        //Featured Products
        recyclerView = findViewById(R.id.featruedRv)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        productList = listOf(
            Product("Century Tuna", "₱ 30.00", R.drawable.century_tuna),
            Product( "Sugar", "₱ 20.00", R.drawable.sugar),
        )
        productAdapter = ProductAdapter(this, productList)
        recyclerView.adapter = productAdapter

        //Snacks
        recyclerView = findViewById(R.id.snacksRv)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        productList = listOf(
            Product("Skyflakes Bundle", "₱ 60.00", R.drawable.skyflakes),
            Product("Cream O Bundle", "₱ 75.00", R.drawable.cream_o_vanilla),
        )
        productAdapter = ProductAdapter(this, productList)
        recyclerView.adapter = productAdapter

        //Sweets
        recyclerView = findViewById(R.id.sweetsRv)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        productList = listOf(
            Product("Toblerone", "₱ 260.00", R.drawable.toblerone),
            Product("M&M's", "₱ 70.00", R.drawable.m_ms),
        )
        productAdapter = ProductAdapter(this, productList)
        recyclerView.adapter = productAdapter

        //Pantry
        recyclerView = findViewById(R.id.pantryRv)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        productList = listOf(
            Product("Youngstown Sardines", "₱ 25.00", R.drawable.youngstown),
            Product( "Purefoods Cornbeef", "₱ 45.00", R.drawable.purefoods_cornedbeef),
        )
        productAdapter = ProductAdapter(this, productList)
        recyclerView.adapter = productAdapter

        //Fresh Produce
        recyclerView = findViewById(R.id.freshproduceRv)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        productList = listOf(
            Product("Eggplant", "₱ 25.00", R.drawable.eggplant),
            Product( "Okra", "₱ 15.00", R.drawable.okra),
        )
        productAdapter = ProductAdapter(this, productList)
        recyclerView.adapter = productAdapter

        //Meats and Seafoods
        recyclerView = findViewById(R.id.meatsandseafoodsRv)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        productList = listOf(
            Product("Pork Chop", "₱ 300.00", R.drawable.pork_chop),
            Product( "Beef Steak", "₱ 350.00", R.drawable.beef_steak),
        )
        productAdapter = ProductAdapter(this, productList)
        recyclerView.adapter = productAdapter

        //Household Essentials
        recyclerView = findViewById(R.id.householdessentialsRv)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        productList = listOf(
            Product("Safeguard Soap", "₱ 40.00", R.drawable.safeguard_soap),
            Product( "Sanicare Tissue", "₱ 120.00", R.drawable.sanicare_tissue),
        )
        productAdapter = ProductAdapter(this, productList)
        recyclerView.adapter = productAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        sliderHandler.removeCallbacks(sliderRunnable)
    }
}
