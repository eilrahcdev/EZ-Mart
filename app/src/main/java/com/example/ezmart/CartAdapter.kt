package com.example.ezmart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(private val cartList: MutableList<Product>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.cartProductImage)
        val productName: TextView = view.findViewById(R.id.cartProductName)
        val productPrice: TextView = view.findViewById(R.id.cartProductPrice)
        val selectCheckBox: CheckBox = view.findViewById(R.id.cbProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = cartList[position]

        holder.productImage.setImageResource(product.imageResId)
        holder.productName.text = product.name
        holder.productPrice.text = product.price

        // Set checkbox state based on product selection
        holder.selectCheckBox.isChecked = product.isSelected

        // Set the blue color for the checkbox
        holder.selectCheckBox.buttonTintList =
            ContextCompat.getColorStateList(holder.itemView.context, R.color.blue)

        // Handle checkbox click event
        holder.selectCheckBox.setOnCheckedChangeListener { _, isChecked ->
            product.isSelected = isChecked
        }
    }

    fun clearCart(context: Context) {
        cartList.clear()
        CartManager.clearCart(context)
        notifyDataSetChanged()
    }

    fun selectAllItems(isChecked: Boolean) {
        for (item in cartList) {
            item.isSelected = isChecked
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = cartList.size
}
