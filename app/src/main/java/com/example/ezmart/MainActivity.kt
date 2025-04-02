package com.example.ezmart

import android.annotation.SuppressLint
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
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.ezmart.utils.UserSession
import com.google.firebase.messaging.FirebaseMessaging

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var cartIbtn: ImageButton
    private lateinit var searchEt: EditText
    private lateinit var dotsLayout: LinearLayout
    private val sliderHandler = Handler(Looper.getMainLooper())
    private lateinit var sliderRunnable: Runnable
    private val dots = mutableListOf<ImageView>()
    private lateinit var unbeatableRecyclerView: RecyclerView
    private lateinit var featuredRecyclerView: RecyclerView
    private lateinit var snacksRecyclerView: RecyclerView
    private lateinit var sweetsRecyclerView: RecyclerView
    private lateinit var pantryRecyclerView: RecyclerView
    private lateinit var freshProduceRecyclerView: RecyclerView
    private lateinit var meatsRecyclerView: RecyclerView
    private lateinit var householdRecyclerView: RecyclerView
    private lateinit var bevergesRecyclerView: RecyclerView
    private lateinit var dairyRecyclerView: RecyclerView

    private lateinit var unbeatableAdapter: ProductAdapter
    private lateinit var featuredAdapter: ProductAdapter
    private lateinit var snacksAdapter: ProductAdapter
    private lateinit var sweetsAdapter: ProductAdapter
    private lateinit var pantryAdapter: ProductAdapter
    private lateinit var freshProduceAdapter: ProductAdapter
    private lateinit var meatsAdapter: ProductAdapter
    private lateinit var householdAdapter: ProductAdapter
    private lateinit var bevergesAdapter: ProductAdapter
    private lateinit var dairyAdapter: ProductAdapter

    private lateinit var viewModel: ProductViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userSession = UserSession(this)
        val loggedInUser = userSession.getUser()

        if (loggedInUser == null) {
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Get FCM Token for Testing
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                println("FCM Token: $token")
            } else {
                println("Failed to get FCM token: ${task.exception}")
            }
        }

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        // Initialize Buttons
        cartIbtn = findViewById(R.id.cartIbtn)
        cartIbtn.setOnClickListener {
            val intent = Intent(this, Cart::class.java)
            startActivity(intent)
        }

        searchEt = findViewById(R.id.searchEt)
        searchEt.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        // Set RecyclerViews
        unbeatableRecyclerView = findViewById(R.id.unbeatableRv)
        featuredRecyclerView = findViewById(R.id.featuredRv)
        snacksRecyclerView = findViewById(R.id.snacksRv)
        sweetsRecyclerView = findViewById(R.id.sweetsRv)
        pantryRecyclerView = findViewById(R.id.pantryRv)
        freshProduceRecyclerView = findViewById(R.id.freshproduceRv)
        meatsRecyclerView = findViewById(R.id.meatsandseafoodsRv)
        householdRecyclerView = findViewById(R.id.householdessentialsRv)
        bevergesRecyclerView = findViewById(R.id.beveragesRv)
        dairyRecyclerView = findViewById(R.id.dairyandpastryRv)

        // Set unique LayoutManagers for each RecyclerView
        fun setupRecyclerView(recyclerView: RecyclerView) {
            recyclerView.layoutManager = GridLayoutManager(this, 2)
        }

        // Limit product list to 2 items
        fun limitProducts(products: List<Product>): List<Product> {
            return if (products.size > 2) products.subList(0, 2) else products
        }

        // Initialize RecyclerViews
        setupRecyclerView(unbeatableRecyclerView)
        setupRecyclerView(featuredRecyclerView)
        setupRecyclerView(snacksRecyclerView)
        setupRecyclerView(sweetsRecyclerView)
        setupRecyclerView(pantryRecyclerView)
        setupRecyclerView(freshProduceRecyclerView)
        setupRecyclerView(meatsRecyclerView)
        setupRecyclerView(householdRecyclerView)
        setupRecyclerView(bevergesRecyclerView)
        setupRecyclerView(dairyRecyclerView)

        // Initialize Adapters
        unbeatableAdapter = ProductAdapter(this, emptyList())
        featuredAdapter = ProductAdapter(this, emptyList())
        snacksAdapter = ProductAdapter(this, emptyList())
        sweetsAdapter = ProductAdapter(this, emptyList())
        pantryAdapter = ProductAdapter(this, emptyList())
        freshProduceAdapter = ProductAdapter(this, emptyList())
        meatsAdapter = ProductAdapter(this, emptyList())
        householdAdapter = ProductAdapter(this, emptyList())
        bevergesAdapter = ProductAdapter(this, emptyList())
        dairyAdapter = ProductAdapter(this, emptyList())

        // Set Adapters
        unbeatableRecyclerView.adapter = unbeatableAdapter
        featuredRecyclerView.adapter = featuredAdapter
        snacksRecyclerView.adapter = snacksAdapter
        sweetsRecyclerView.adapter = sweetsAdapter
        pantryRecyclerView.adapter = pantryAdapter
        freshProduceRecyclerView.adapter = freshProduceAdapter
        meatsRecyclerView.adapter = meatsAdapter
        householdRecyclerView.adapter = householdAdapter
        bevergesRecyclerView.adapter = bevergesAdapter
        dairyRecyclerView.adapter = dairyAdapter

        // Fetch products from the API
        viewModel.fetchProducts()

        // Observe products LiveData
        viewModel.products.observe(this) { products ->
            unbeatableAdapter.updateProductList(limitProducts(products.filter { it.category == "Unbeatable Prices" }))
            featuredAdapter.updateProductList(limitProducts(products.filter { it.category == "Featured Products" }))
            snacksAdapter.updateProductList(limitProducts(products.filter { it.category == "Snacks" }))
            sweetsAdapter.updateProductList(limitProducts(products.filter { it.category == "Sweets" }))
            pantryAdapter.updateProductList(limitProducts(products.filter { it.category == "Pantry" }))
            freshProduceAdapter.updateProductList(limitProducts(products.filter { it.category == "Fresh Produce" }))
            meatsAdapter.updateProductList(limitProducts(products.filter { it.category == "Meats and Seafoods" }))
            householdAdapter.updateProductList(limitProducts(products.filter { it.category == "Household Essentials" }))
            bevergesAdapter.updateProductList(limitProducts(products.filter { it.category == "Beverages" }))
            dairyAdapter.updateProductList(limitProducts(products.filter { it.category == "Dairy and Pastry" }))
        }

        // Observe error messages
        viewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }

        // Set up additional UI elements
        setupSeeMoreTextViews()
        setupNavigation()
        setupImageSlider()
    }

    fun setupSeeMoreTextViews() {
        val seeMoreMappings = mapOf(
            R.id.unbeatable_seemoreTv to UnbeatablePrices::class.java,
            R.id.featured_seemoreTv to FeaturedProducts::class.java,
            R.id.snacks_seemoreTv to Snacks::class.java,
            R.id.beverages_seemoreTv to Beverages::class.java,
            R.id.sweets_seemoreTv to Sweets::class.java,
            R.id.pantry_seemoreTv to Pantry::class.java,
            R.id.dairy_seemoreTv to DairyandPastry::class.java,
            R.id.freshproduce_seemoreTv to FreshProduce::class.java,
            R.id.meatsandseafoods_seemoreTv to MeatsandSeafoods::class.java,
            R.id.household_seemoreTv to HouseholdEssentials::class.java
        )

        seeMoreMappings.forEach { (textViewId, destination) ->
            makeSpannable(findViewById(textViewId), destination)
        }
    }

    fun makeSpannable(textView: TextView, destination: Class<*>) {
        val spannableString = SpannableString("See More =>").apply {
            setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(widget.context, destination)
                    widget.context.startActivity(intent)
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

    fun setupImageSlider() {
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

    fun updateDotsIndicator(selectedIndex: Int) {
        dots.forEachIndexed { index, imageView ->
            imageView.setImageDrawable(
                ContextCompat.getDrawable(this, if (index == selectedIndex) R.drawable.active_dot else R.drawable.non_active_dot)
            )
        }
    }


    fun setupNavigation() {
        val navigationItems = listOf(
            R.id.homeTab to Pair(R.id.homeIcon, R.id.homeText),
            R.id.categoriesTab to Pair(R.id.categoriesIcon, R.id.categoriesText),
            R.id.ordersTab to Pair(R.id.ordersIcon, R.id.ordersText),
            R.id.profileTab to Pair(R.id.profileIcon, R.id.profileText)
        )

        // Set Home tab as active
        setDefaultHomeTab()


        navigationItems.forEach { (tabId, iconTextPair) ->
            val (iconId, textId) = iconTextPair
            findViewById<LinearLayout>(tabId).setOnClickListener {
                resetNavigationColors()
                findViewById<ImageView>(iconId).setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.blue
                    )
                )
                findViewById<TextView>(textId).setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.blue
                    )
                )
                startActivity(Intent(this, getDestinationClass(tabId)))
            }
        }
    }

    // Highlight Home tab as active
    private fun setDefaultHomeTab() {
        findViewById<ImageView>(R.id.homeIcon).setColorFilter(
            ContextCompat.getColor(this, R.color.blue)
        )
        findViewById<TextView>(R.id.homeText).setTextColor(
            ContextCompat.getColor(this, R.color.blue)
        )
    }

    fun getDestinationClass(tabId: Int): Class<*> {
        return when (tabId) {
            R.id.categoriesTab -> Categories::class.java
            R.id.ordersTab -> Orders::class.java
            R.id.profileTab -> Profile::class.java
            else -> MainActivity::class.java
        }
    }

    fun resetNavigationColors() {
        listOf(R.id.homeIcon, R.id.categoriesIcon, R.id.ordersIcon, R.id.profileIcon).forEach {
            findViewById<ImageView>(it).setColorFilter(ContextCompat.getColor(this, R.color.black))
        }
        listOf(R.id.homeText, R.id.categoriesText, R.id.ordersText, R.id.profileText).forEach {
            findViewById<TextView>(it).setTextColor(ContextCompat.getColor(this, R.color.black))
        }
    }
}