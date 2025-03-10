package com.example.ezmart

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var cartIbtn: ImageButton
    private lateinit var searchEt: EditText
    private lateinit var dotsLayout: LinearLayout
    private val sliderHandler = Handler(Looper.getMainLooper())
    private lateinit var sliderRunnable: Runnable
    private val dots = mutableListOf<ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        setupWindowInsets()
        setupButtons()
        setupSeeMoreTextViews()
        setupNavigation()
        setupImageSlider()
        setupProductCategories()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupButtons() {
        cartIbtn = findViewById(R.id.cartIbtn)
        cartIbtn.setOnClickListener {
            navigateTo(Cart::class.java)
        }

        searchEt = findViewById(R.id.searchEt)
        searchEt.setOnClickListener {
            navigateTo(SearchActivity::class.java)
        }
    }

    private fun setupSeeMoreTextViews() {
        val seeMoreMappings = mapOf(
            R.id.unbeatable_seemoreTv to UnbeatablePrices::class.java,
            R.id.featured_seemoreTv to FeaturedProducts::class.java,
            R.id.snacks_seemoreTv to Snacks::class.java,
            R.id.sweets_seemoreTv to Sweets::class.java,
            R.id.pantry_seemoreTv to Pantry::class.java,
            R.id.freshproduce_seemoreTv to FreshProduce::class.java,
            R.id.meatsandseafoods_seemoreTv to MeatsandSeafoods::class.java,
            R.id.household_seemoreTv to HouseholdEssentials::class.java
        )

        seeMoreMappings.forEach { (textViewId, destination) ->
            makeSpannable(findViewById(textViewId), destination)
        }
    }

    private fun makeSpannable(textView: TextView, destination: Class<*>) {
        val spannableString = SpannableString("See More =>").apply {
            setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    navigateTo(destination)
                }
                override fun updateDrawState(ds: android.text.TextPaint) {
                    ds.isUnderlineText = false
                }
            }, 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(Color.BLUE), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        textView.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }
    }

    private fun setupNavigation() {
        val navigationItems = listOf(
            R.id.homeTab to Pair(R.id.homeIcon, R.id.homeText),
            R.id.categoriesTab to Pair(R.id.categoriesIcon, R.id.categoriesText),
            R.id.ordersTab to Pair(R.id.ordersIcon, R.id.ordersText),
            R.id.profileTab to Pair(R.id.profileIcon, R.id.profileText)
        )

        navigationItems.forEach { (tabId, iconTextPair) ->
            val (iconId, textId) = iconTextPair
            findViewById<LinearLayout>(tabId).setOnClickListener {
                resetNavigationColors()
                findViewById<ImageView>(iconId).setColorFilter(resources.getColor(R.color.blue))
                findViewById<TextView>(textId).setTextColor(resources.getColor(R.color.blue))
                navigateTo(getDestinationClass(tabId))
            }
        }
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
            findViewById<ImageView>(it).setColorFilter(resources.getColor(R.color.black))
        }
        listOf(R.id.homeText, R.id.categoriesText, R.id.ordersText, R.id.profileText).forEach {
            findViewById<TextView>(it).setTextColor(resources.getColor(R.color.black))
        }
    }

    private fun setupImageSlider() {
        val imageList = listOf(R.drawable.ad1, R.drawable.ad2, R.drawable.ad3)
        val loopedList = listOf(imageList.last()) + imageList + listOf(imageList.first())

        viewPager = findViewById(R.id.viewPager)
        dotsLayout = findViewById(R.id.dotsLayout)
        setupDotsIndicator(imageList.size)

        viewPager.adapter = ImageSliderAdapter(loopedList)
        viewPager.setCurrentItem(1, false)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 3000)

                if (position == loopedList.size - 1) {
                    viewPager.post { viewPager.setCurrentItem(1, false) }
                } else if (position == 0) {
                    viewPager.post { viewPager.setCurrentItem(loopedList.size - 2, false) }
                }

                updateDotsIndicator(position - 1)
            }
        })

        sliderRunnable = Runnable {
            viewPager.setCurrentItem(viewPager.currentItem + 1, true)
            sliderHandler.postDelayed(sliderRunnable, 3000)
        }
        sliderHandler.postDelayed(sliderRunnable, 3000)
    }

    private fun setupDotsIndicator(count: Int) {
        dotsLayout.removeAllViews()
        dots.clear()
        repeat(count) {
            val dot = ImageView(this).apply {
                setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.non_active_dot))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(8, 0, 8, 0) }
            }
            dotsLayout.addView(dot)
            dots.add(dot)
        }
        dots.firstOrNull()?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_dot))
    }

    private fun updateDotsIndicator(selectedIndex: Int) {
        dots.forEachIndexed { index, imageView ->
            imageView.setImageDrawable(
                ContextCompat.getDrawable(this, if (index == selectedIndex) R.drawable.active_dot else R.drawable.non_active_dot)
            )
        }
    }

    private fun setupProductCategories() {
        val categories: List<Pair<Int, List<Product>>> = listOf(
            Pair(R.id.unbeatableRv, listOf(
                Product("Nissin Wafer Choco", 50.00, R.drawable.nissin_wafer),
                Product("Knorr Sinigang Mix", 15.00, R.drawable.knorr_sinigang_original_mix)
            )),
            Pair(R.id.featruedRv, listOf(
                Product("Century Tuna", 30.00, R.drawable.century_tuna),
                Product("Sugar", 20.00, R.drawable.sugar)
            )),
            Pair(R.id.snacksRv, listOf(
                Product("Skyflakes Bundle", 60.00, R.drawable.skyflakes),
                Product("Cream O Bundle", 75.00, R.drawable.cream_o_vanilla)
            )),
            Pair(R.id.sweetsRv, listOf(
                Product("Toblerone", 260.00, R.drawable.toblerone),
                Product("M&M's", 70.00, R.drawable.m_ms)
            )),
            Pair(R.id.pantryRv, listOf(
                Product("Youngstown Sardines", 25.00, R.drawable.youngstown),
                Product("Purefoods Cornbeef", 45.00, R.drawable.purefoods_cornedbeef)
            )),
            Pair(R.id.freshproduceRv, listOf(
                Product("Eggplant", 25.00, R.drawable.eggplant),
                Product("Okra", 15.00, R.drawable.okra)
            )),
            Pair(R.id.meatsandseafoodsRv, listOf(
                Product("Pork Chop", 300.00, R.drawable.pork_chop),
                Product("Beef Steak", 350.00, R.drawable.beef_steak)
            )),
            Pair(R.id.householdessentialsRv, listOf(
                Product("Safeguard Soap", 40.00, R.drawable.safeguard_soap),
                Product("Sanicare Tissue", 120.00, R.drawable.sanicare_tissue)
            ))
        )

        categories.forEach { category ->
            val recyclerViewId = category.first
            val products = category.second
            setupRecyclerView(recyclerViewId, products)
        }
    }


    private fun setupRecyclerView(recyclerViewId: Int, products: List<Product>) {
        findViewById<RecyclerView>(recyclerViewId).apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = ProductAdapter(this@MainActivity, products)
        }
    }

    private fun navigateTo(destination: Class<*>) {
        startActivity(Intent(this, destination))
        finish()
    }
}
