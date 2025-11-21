package com.example.grabapp.ui.home.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.grabapp.ui.home.fragment.HistoryFragment
import com.example.grabapp.ui.home.fragment.MainFragment

class MainPageAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            FRAGMENT_MAIN -> MainFragment()
            FRAGMENT_ORDER_HISTORY -> HistoryFragment()
            else -> MainFragment()
        }
    }

    override fun getItemCount() = 2

    companion object {
        const val FRAGMENT_MAIN = 0
        const val FRAGMENT_ORDER_HISTORY = 1

    }
}