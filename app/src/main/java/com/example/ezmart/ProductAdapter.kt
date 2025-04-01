package com.example.ezmart

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog

class ProductAdapter(
    private val context: Context,
    private var productList: List<Product>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.productImage)
        val productName: TextView = view.findViewById(R.id.productName)
        val productPrice: TextView = view.findViewById(R.id.productPrice)
        val addToCartBtn: Button = view.findViewById(R.id.addToCartButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        // Load product image
        Glide.with(context).load(product.image).into(holder.productImage)

        // Set product details
        holder.productName.text = product.name
        holder.productPrice.text = "₱ %.2f".format(product.price)

        // Handle item click to open product details in ModalBottomSheet Activity
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ModalBottomSheet::class.java)
            intent.putExtra("product", product)
            context.startActivity(intent)
        }

        // Handle Add to Cart button click to show BottomSheet
        holder.addToCartBtn.setOnClickListener {
            showAddToCartBottomSheet(product)
        }
    }

    override fun getItemCount(): Int = productList.size

    // Function to update the adapter's data and refresh RecyclerView
    fun updateProductList(newProductList: List<Product>) {
        productList = newProductList
        notifyDataSetChanged()
    }

    // Function to show Add to Cart Bottom Sheet
    private fun showAddToCartBottomSheet(product: Product) {
        val activity = context as? AppCompatActivity
        if (activity == null || activity.isFinishing) {
            return // Prevent crash if context is invalid
        }

        val dialogView = LayoutInflater.from(activity).inflate(R.layout.bottomsheet_addtocart, null)
        val bottomSheetDialog = BottomSheetDialog(activity, R.style.BottomSheetDialogTheme)
        bottomSheetDialog.setContentView(dialogView)

        // Ensure it expands properly
        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.post {
                d.behavior.peekHeight = bottomSheet.height
            }
        }

        // Bind product details in BottomSheet
        val productImage: ImageView = dialogView.findViewById(R.id.productIv)
        val productName: TextView = dialogView.findViewById(R.id.productNameTv)
        val productPrice: TextView = dialogView.findViewById(R.id.productPriceTv)
        val productStock: TextView = dialogView.findViewById(R.id.productStockTv)
        val quantityTv: TextView = dialogView.findViewById(R.id.tvCounter_bta)
        val minusButton: ImageButton = dialogView.findViewById(R.id.btnMinus_bta)
        val plusButton: ImageButton = dialogView.findViewById(R.id.btnPlus_bta)
        val confirmAddToCartBtn: Button = dialogView.findViewById(R.id.addToCartBtn)

        // Set product details
        Glide.with(activity).load(product.image).into(productImage)
        productName.text = product.name
        productPrice.text = "₱ %.2f".format(product.price)
        productStock.text = "Stock: ${product.stock}"

        // Quantity Counter Logic
        var quantity = 1
        quantityTv.text = quantity.toString()

        minusButton.setOnClickListener {
            if (quantity > 1) {
                quantity--
                quantityTv.text = quantity.toString()
            }
        }

        plusButton.setOnClickListener {
            if (product.stock > quantity) {
                quantity++
                quantityTv.text = quantity.toString()
            }
        }

        // Handle Add to Cart Confirmation
        confirmAddToCartBtn.setOnClickListener {
            CartManager.addToCart(context, product, quantity)
            bottomSheetDialog.dismiss()
            Toast.makeText(context, "${product.name} added to cart", Toast.LENGTH_SHORT).show()
        }

        // **SHOW DIALOG**
        bottomSheetDialog.show()
    }
}