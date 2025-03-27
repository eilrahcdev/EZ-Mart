    package com.example.ezmart

    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.TextView
    import androidx.recyclerview.widget.RecyclerView
    import com.example.ezmart.models.OrderModel

    class OrderAdapter(private var orderList: MutableList<OrderModel>) :
        RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

        class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val productDetails: TextView = view.findViewById(R.id.product_name)
            val totalAmount: TextView = view.findViewById(R.id.total_amount)
            val paymentMethod: TextView = view.findViewById(R.id.payment_method)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order, parent, false)
            return OrderViewHolder(view)
        }

        override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
            val order = orderList[position]

            // Display multiple products in one order as a single string
            val productDetailsText = order.products.joinToString("\n") { product ->
                "• ${product.name} (Qty: ${product.quantity})"
            }

            holder.productDetails.text = productDetailsText
            holder.totalAmount.text = "Total: ₱ ${order.totalAmount}"
            holder.paymentMethod.text = "Payment Method: ${order.paymentMethod}"
        }

        override fun getItemCount(): Int = orderList.size

        fun updateOrders(newOrders: List<OrderModel>) {
            orderList.clear()
            orderList.addAll(newOrders)
            notifyDataSetChanged()
        }
    }

