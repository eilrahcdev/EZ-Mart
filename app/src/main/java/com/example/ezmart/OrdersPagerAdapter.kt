package com.example.ezmart

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ezmart.models.OrderModel

class OrdersPagerAdapter(activity: AppCompatActivity, orders: List<OrderModel>) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 4 // Number of tabs

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PendingOrdersFragment()
            1 -> ProcessingOrdersFragment()
            2 -> CompletedOrdersFragment()
            3 -> CancelledOrdersFragment()
            else -> PendingOrdersFragment()
        }
    }
}
