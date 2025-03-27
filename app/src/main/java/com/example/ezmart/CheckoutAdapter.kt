package com.example.ezmart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CheckoutAdapter(private val productList: List<Product>) :
    RecyclerView.Adapter<CheckoutAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.checkout_product_image)
        val productName: TextView = itemView.findViewById(R.id.checkout_product_name)
        val productPrice: TextView = itemView.findViewById(R.id.checkout_product_price)
        val productQuantity: TextView = itemView.findViewById(R.id.checkout_product_quantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checkout_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]

        holder.productName.text = product.name
        holder.productPrice.text = "₱ %.2f".format(product.price)
        holder.productQuantity.text = "x${product.quantity}"

        Glide.with(holder.itemView.context)
            .load(product.image)
            .into(holder.productImage)
    }

    override fun getItemCount(): Int = productList.size
}
