package com.example.ezmart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartAdapter(
    private val cartList: MutableList<Product>,
    private val onTotalAmountUpdated: (Double) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.cartProductImage)
        val productName: TextView = view.findViewById(R.id.cartProductName)
        val productPrice: TextView = view.findViewById(R.id.cartProductPrice)
        val selectCheckBox: CheckBox = view.findViewById(R.id.cbProduct)
        val btnPlus: ImageButton = view.findViewById(R.id.btnPlus)
        val btnMinus: ImageButton = view.findViewById(R.id.btnMinus)
        val quantityText: TextView = view.findViewById(R.id.tvCounter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = cartList[position]

        Glide.with(holder.itemView.context)
            .load(product.image) // URL
            .into(holder.productImage)

        holder.productName.text = product.name
        holder.productPrice.text = "₱ %.2f".format(product.price * product.quantity)
        holder.quantityText.text = product.quantity.toString()

        // Prevent multiple calls to onCheckedChanged when setting the state
        holder.selectCheckBox.setOnCheckedChangeListener(null)
        holder.selectCheckBox.isChecked = product.isSelected

        // Apply blue tint to checkbox
        holder.selectCheckBox.buttonTintList =
            ContextCompat.getColorStateList(holder.itemView.context, R.color.blue)

        // Checkbox click event
        holder.selectCheckBox.setOnCheckedChangeListener { _, isChecked ->
            product.isSelected = isChecked
            onTotalAmountUpdated(getTotalAmount())
        }

        // Plus button: Increase quantity
        holder.btnPlus.setOnClickListener {
            product.quantity++
            holder.quantityText.text = product.quantity.toString()
            holder.productPrice.text = "₱ %.2f".format(product.price * product.quantity)

            // Update the cart in shared preferences
            CartManager.updateCart(holder.itemView.context, cartList)
            notifyDataSetChanged() // Refresh UI
            onTotalAmountUpdated(getTotalAmount())
        }

        holder.btnMinus.setOnClickListener {
            if (product.quantity > 1) {
                product.quantity--
                holder.quantityText.text = product.quantity.toString()
                holder.productPrice.text = "₱ %.2f".format(product.price * product.quantity)

                // Update the cart in shared preferences
                CartManager.updateCart(holder.itemView.context, cartList)
                notifyDataSetChanged() // Refresh UI
                onTotalAmountUpdated(getTotalAmount())
            }
        }
    }

    // Remove selected items from cart
    fun removeSelectedItems(context: Context) {
        cartList.removeAll { it.isSelected }
        CartManager.updateCart(context, cartList) // Save updated cart
        notifyDataSetChanged()
        onTotalAmountUpdated(getTotalAmount())
    }

    fun selectAllItems(isChecked: Boolean) {
        cartList.forEach { it.isSelected = isChecked }
        notifyDataSetChanged()
        onTotalAmountUpdated(getTotalAmount())
    }

    fun getTotalAmount(): Double {
        return cartList.filter { it.isSelected }.sumOf { it.price * it.quantity }
    }

    fun getSelectedItems(): List<Product> {
        return cartList.filter { it.isSelected }
    }

    override fun getItemCount(): Int = cartList.size
}