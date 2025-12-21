package com.example.grabapp.ui.address_selection.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.grabapp.ui.address_selection.fragment.CheckDirectionFragment
import com.example.grabapp.ui.address_selection.fragment.CheckOrderFragment
import com.example.grabapp.ui.address_selection.fragment.DetailOrderFragment
import com.example.grabapp.ui.address_selection.fragment.MainOrderFragment
import com.example.grabapp.ui.address_selection.fragment.PaymentFragment

class AddressSelectionPageAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            FRAGMENT_MAIN -> MainOrderFragment()
            FRAGMENT_DETAIL_ORDER -> DetailOrderFragment()
            FRAGMENT_CHECK_DIRECTION-> CheckDirectionFragment()
            FRAGMENT_PAYMENT -> PaymentFragment()
            else -> MainOrderFragment()
        }
    }

    override fun getItemCount() = 4

    companion object {
        const val FRAGMENT_MAIN = 0
        const val FRAGMENT_DETAIL_ORDER = 1
        const val FRAGMENT_CHECK_DIRECTION = 2
        const val FRAGMENT_PAYMENT = 3
    }
}