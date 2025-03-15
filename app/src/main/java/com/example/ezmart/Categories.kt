package com.example.ezmart

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.AppBarLayout

@Suppress("DEPRECATION")
class Categories : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.categories)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.categories_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Initialization of Buttons and setting of functionality
        val featuredproductsBtn = findViewById<Button>(R.id.featuredproductsBtn)
        featuredproductsBtn.setOnClickListener {
            startActivity(Intent(this, FeaturedProducts::class.java))
            finish()
        }

        val freshproduceBtn = findViewById<Button>(R.id.freshproduceBtn)
        freshproduceBtn.setOnClickListener {
            startActivity(Intent(this, FreshProduce::class.java))
            finish()
        }

        val pantryBtn = findViewById<Button>(R.id.pantryBtn)
        pantryBtn.setOnClickListener {
            startActivity(Intent(this, Pantry::class.java))
            finish()
        }

        val snacksBtn = findViewById<Button>(R.id.snacksBtn)
        snacksBtn.setOnClickListener {
            startActivity(Intent(this, Snacks::class.java))
            finish()
        }
        val beveragesBtn = findViewById<Button>(R.id.beveragesBtn)
        beveragesBtn.setOnClickListener {
            startActivity(Intent(this, Beverages::class.java))
            finish()
        }
        val dairyBtn = findViewById<Button>(R.id.dairyandpastryBtn)
        dairyBtn.setOnClickListener {
            startActivity(Intent(this, DairyandPastry::class.java))
            finish()
        }

        val meatsBtn = findViewById<Button>(R.id.meatsBtn)
        meatsBtn.setOnClickListener {
            startActivity(Intent(this, MeatsandSeafoods::class.java))
            finish()
        }

        val sweetsBtn = findViewById<Button>(R.id.sweetsBtn)
        sweetsBtn.setOnClickListener {
            startActivity(Intent(this, Sweets::class.java))
            finish()
        }

        val householdBtn = findViewById<Button>(R.id.householdessentialsBtn)
        householdBtn.setOnClickListener {
            startActivity(Intent(this, HouseholdEssentials::class.java))
            finish()
        }

        //Back Button
        val backbutton = findViewById<ImageButton>(R.id.backBtn_categories)
        backbutton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
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
        categoriesIcon.setColorFilter(resources.getColor(R.color.blue))
        categoriesText.setTextColor(resources.getColor(R.color.blue))

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
