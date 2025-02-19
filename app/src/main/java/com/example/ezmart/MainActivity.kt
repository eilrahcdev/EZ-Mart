package com.example.ezmart

import ImageSliderAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
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

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nav_home

        val imageList = listOf(R.drawable.ad1, R.drawable.ad2, R.drawable.ad3)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val tabIndicator: TabLayout = findViewById(R.id.tabIndicator)

        val adapter = ImageSliderAdapter(imageList)
        viewPager.adapter = adapter

        // Attach the dots indicator to the ViewPager
        TabLayoutMediator(tabIndicator, viewPager) { _, _ -> }.attach()

    }
}