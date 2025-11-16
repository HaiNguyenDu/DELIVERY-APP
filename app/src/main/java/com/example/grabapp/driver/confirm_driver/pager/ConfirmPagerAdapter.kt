package com.example.grabapp.driver.confirm_driver.pager

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ConfirmPagerAdapter(
    activity: FragmentActivity
) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        return ConfirmPagerFragment().apply {
            arguments = Bundle().apply {
                putInt(POSITION, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return ConfirmPager.entries.size
    }

    companion object {
        const val POSITION = "position"
    }
}
