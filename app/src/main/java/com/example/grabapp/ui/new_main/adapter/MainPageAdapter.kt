package com.example.grabapp.ui.new_main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.grabapp.ui.new_main.fragment.MainFragment

class MainPageAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            FRAGMENT_MAIN -> MainFragment()
            FRAGMENT_ADD_IMAGE -> MainFragment()
            else -> MainFragment()
        }
    }

    override fun getItemCount() = FRAGMENT_COUNT

    companion object {
        const val FRAGMENT_MAIN = 0
        const val FRAGMENT_ADD_IMAGE = 1

        const val FRAGMENT_COUNT = 2
    }
}