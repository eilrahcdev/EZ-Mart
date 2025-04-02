package com.example.ezmart

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ezmart.models.OrderModel

class OrdersPagerAdapter(
    activity: AppCompatActivity,
    private val pendingOrders: List<OrderModel>,
    private val topayOrders: List<OrderModel>,
    private val readytopickupOrders: List<OrderModel>,
    private val completedOrders: List<OrderModel>,
    private val cancelledOrders: List<OrderModel>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 5 // Number of tabs

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PendingOrdersFragment.newInstance()
            1 -> ToPayOrdersFragment.newInstance()
            2 -> ReadyToPickUpOrdersFragment.newInstance()
            3 -> CompletedOrdersFragment.newInstance()
            4 -> CancelledOrdersFragment.newInstance()
            else -> PendingOrdersFragment.newInstance()
        }
    }

}
