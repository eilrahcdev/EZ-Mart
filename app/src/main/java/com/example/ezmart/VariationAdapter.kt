package com.example.ezmart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class VariationAdapter(
    private val variations: List<Variation>,
    private val onItemClick: (Variation) -> Unit
) : RecyclerView.Adapter<VariationAdapter.VariationViewHolder>() {

    inner class VariationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.ivVariation)
        private val textView: TextView = itemView.findViewById(R.id.tvVariationName)

        fun bind(variation: Variation) {
            // Load image using Glide
            Glide.with(itemView.context)
                .load(variation.image)
                .into(imageView)

            textView.text = variation.name
            itemView.setOnClickListener { onItemClick(variation) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_variation, parent, false)
        return VariationViewHolder(view)
    }

    override fun onBindViewHolder(holder: VariationViewHolder, position: Int) {
        holder.bind(variations[position])
    }

    override fun getItemCount(): Int = variations.size
}
