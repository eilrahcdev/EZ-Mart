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
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        // Initialize category buttons
        setupCategoryButtons()

        // Back Button
        val backButton = findViewById<ImageButton>(R.id.backBtn_categories)
        backButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Setup bottom navigation
        setupNavigation()
    }

    private fun setupCategoryButtons() {
        val categoryButtons = mapOf(
            R.id.featuredproductsBtn to FeaturedProducts::class.java,
            R.id.freshproduceBtn to FreshProduce::class.java,
            R.id.pantryBtn to Pantry::class.java,
            R.id.snacksBtn to Snacks::class.java,
            R.id.beveragesBtn to Beverages::class.java,
            R.id.dairyandpastryBtn to DairyandPastry::class.java,
            R.id.meatsBtn to MeatsandSeafoods::class.java,
            R.id.sweetsBtn to Sweets::class.java,
            R.id.householdessentialsBtn to HouseholdEssentials::class.java
        )

        categoryButtons.forEach { (buttonId, destination) ->
            findViewById<Button>(buttonId).setOnClickListener {
                startActivity(Intent(this, destination))
                finish()
            }
        }
    }

    // Setup bottom navigation
    private fun setupNavigation() {
        val navigationItems = listOf(
            R.id.homeTab to Pair(R.id.homeIcon, R.id.homeText),
            R.id.categoriesTab to Pair(R.id.categoriesIcon, R.id.categoriesText),
            R.id.ordersTab to Pair(R.id.ordersIcon, R.id.ordersText),
            R.id.profileTab to Pair(R.id.profileIcon, R.id.profileText)
        )

        // Set Categories tab as default
        setDefaultCategoriesTab()

        navigationItems.forEach { (tabId, iconTextPair) ->
            val (iconId, textId) = iconTextPair
            findViewById<LinearLayout>(tabId).setOnClickListener {
                resetNavigationColors()
                findViewById<ImageView>(iconId).setColorFilter(
                    ContextCompat.getColor(this, R.color.blue)
                )
                findViewById<TextView>(textId).setTextColor(
                    ContextCompat.getColor(this, R.color.blue)
                )
                startActivity(Intent(this, getDestinationClass(tabId)))
            }
        }
    }

    // Set Categories tab as default
    private fun setDefaultCategoriesTab() {
        findViewById<ImageView>(R.id.categoriesIcon).setColorFilter(
            ContextCompat.getColor(this, R.color.blue)
        )
        findViewById<TextView>(R.id.categoriesText).setTextColor(
            ContextCompat.getColor(this, R.color.blue)
        )
    }

    private fun getDestinationClass(tabId: Int): Class<*> {
        return when (tabId) {
            R.id.categoriesTab -> Categories::class.java
            R.id.ordersTab -> Orders::class.java
            R.id.profileTab -> Profile::class.java
            else -> MainActivity::class.java
        }
    }

    private fun resetNavigationColors() {
        listOf(R.id.homeIcon, R.id.categoriesIcon, R.id.ordersIcon, R.id.profileIcon).forEach {
            findViewById<ImageView>(it).setColorFilter(ContextCompat.getColor(this, R.color.black))
        }
        listOf(R.id.homeText, R.id.categoriesText, R.id.ordersText, R.id.profileText).forEach {
            findViewById<TextView>(it).setTextColor(ContextCompat.getColor(this, R.color.black))
        }
    }
}
