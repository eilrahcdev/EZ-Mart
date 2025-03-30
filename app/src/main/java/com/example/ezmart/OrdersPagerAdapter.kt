package com.example.ezmart

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ezmart.models.OrderModel

class OrdersPagerAdapter(
    activity: AppCompatActivity,
    private val pendingOrders: List<OrderModel>,
    private val processingOrders: List<OrderModel>,
    private val completedOrders: List<OrderModel>,
    private val cancelledOrders: List<OrderModel>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 4 // Number of tabs

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PendingOrdersFragment.newInstance()
            1 -> ToPayOrdersFragment.newInstance()
            2 -> CompletedOrdersFragment.newInstance()
            3 -> CancelledOrdersFragment.newInstance()
            else -> PendingOrdersFragment.newInstance()
        }
    }

}
