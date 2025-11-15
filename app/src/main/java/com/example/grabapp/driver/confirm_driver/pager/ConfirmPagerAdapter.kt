package com.example.grabapp.driver.confirm_driver.pager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ConfirmPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return ConfirmPagerFragment()
    }

    override fun getItemCount(): Int {
        return 0
    }
}
