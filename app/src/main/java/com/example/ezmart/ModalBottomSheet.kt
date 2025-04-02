package com.example.ezmart

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog

@Suppress("DEPRECATION")
class ModalBottomSheet : AppCompatActivity() {

    private lateinit var actionAddToCartBtn: Button
    private lateinit var actionBuyNowBtn: Button
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var selectedProduct: Product? = null
    private var quantityAddToCart: Int = 1
    private var quantityBuyNow: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.actionproduct_activity)

        val backBtn: ImageView = findViewById(R.id.backBtn_action)
        val cartBtn: ImageView = findViewById(R.id.cartIbtn_action)

        backBtn.setOnClickListener { finish() }
        cartBtn.setOnClickListener {
            startActivity(Intent(this, Cart::class.java))
            finish()
        }

        selectedProduct = intent.getParcelableExtra("product")
        selectedProduct?.let { setupProductDetails(it) }

        actionAddToCartBtn = findViewById(R.id.action_addToCartBtn)
        actionBuyNowBtn = findViewById(R.id.action_BuyBtn)

        actionAddToCartBtn.setOnClickListener {
            showBottomSheetWithProductDetails(R.layout.bottomsheet_addtocart, true)
        }

        actionBuyNowBtn.setOnClickListener {
            showBottomSheetWithProductDetails(R.layout.bottomsheet_buy, false)
        }
    }

    private fun setupProductDetails(product: Product) {
        val productImage: ImageView = findViewById(R.id.action_productImgIv)
        val productName: TextView = findViewById(R.id.action_productNameTv)
        val productPrice: TextView = findViewById(R.id.action_productPriceTv)
        val productStock: TextView = findViewById(R.id.action_stockTv)

        Glide.with(this).load(product.image).into(productImage)
        productName.text = product.name
        productPrice.text = "₱ %.2f".format(product.price)
        productStock.text = "Stock: ${product.stock}"
    }

    private fun showBottomSheetWithProductDetails(layoutResId: Int, isAddToCart: Boolean) {
        if (selectedProduct == null) return

        val dialogView: View = LayoutInflater.from(this).inflate(layoutResId, null)
        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme).apply {
            setContentView(dialogView)
            setOnDismissListener { bottomSheetDialog = null }
        }

        val productImage: ImageView? = dialogView.findViewById(R.id.productIv)
        val productName: TextView? = dialogView.findViewById(R.id.productNameTv)
        val productPrice: TextView? = dialogView.findViewById(R.id.productPriceTv)
        val productStock: TextView? = dialogView.findViewById(R.id.productStockTv)

        productImage?.let { Glide.with(this).load(selectedProduct!!.image).into(it) }
        productName?.text = selectedProduct!!.name
        productPrice?.text = "₱ %.2f".format(selectedProduct!!.price)
        productStock?.text = "Stock: ${selectedProduct!!.stock}"

        setupQuantityCounter(dialogView, isAddToCart)

        val confirmButton: Button? = dialogView.findViewById(
            if (isAddToCart) R.id.addToCartBtn else R.id.buyNowBtn
        )

        confirmButton?.setOnClickListener {
            if (isAddToCart) {
                addToCart()
            } else {
                buyNow()
            }
        }

        bottomSheetDialog?.show()
    }

    private fun setupQuantityCounter(view: View, isAddToCart: Boolean) {
        val quantityTv: TextView? = view.findViewById(
            if (isAddToCart) R.id.tvCounter_bta else R.id.tvCounter_btb
        )
        val minusButton: ImageButton? = view.findViewById(
            if (isAddToCart) R.id.btnMinus_bta else R.id.btnMinus_btb
        )
        val plusButton: ImageButton? = view.findViewById(
            if (isAddToCart) R.id.btnPlus_bta else R.id.btnPlus_btb
        )

        val quantity = if (isAddToCart) quantityAddToCart else quantityBuyNow
        quantityTv?.text = quantity.toString()

        minusButton?.setOnClickListener {
            if (isAddToCart && quantityAddToCart > 1) {
                quantityAddToCart--
                quantityTv?.text = quantityAddToCart.toString()
            } else if (!isAddToCart && quantityBuyNow > 1) {
                quantityBuyNow--
                quantityTv?.text = quantityBuyNow.toString()
            }
        }

        plusButton?.setOnClickListener {
            if (selectedProduct!!.stock > (if (isAddToCart) quantityAddToCart else quantityBuyNow)) {
                if (isAddToCart) {
                    quantityAddToCart++
                    quantityTv?.text = quantityAddToCart.toString()
                } else {
                    quantityBuyNow++
                    quantityTv?.text = quantityBuyNow.toString()
                }
            } else {
                Toast.makeText(this, "Cannot add more than stock!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addToCart() {
        selectedProduct?.let { product ->
            CartManager.addToCart(this, product, quantityAddToCart)
            Toast.makeText(this, "${product.name} added to cart!", Toast.LENGTH_SHORT).show()
            bottomSheetDialog?.dismiss()
        }
    }

    private fun buyNow() {
        selectedProduct?.let {
            val selectedProductsList = arrayListOf(it.copy(quantity = quantityBuyNow))

            val intent = Intent(this, Checkout::class.java).apply {
                putParcelableArrayListExtra("selected_products", selectedProductsList)
            }

            startActivity(intent)
            bottomSheetDialog?.dismiss()
        }
    }
}
