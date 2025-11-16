package com.example.grabapp.driver.register.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.grabapp.driver.register.data.RegisterStep

class RegisterPageAdapter(
    activity: FragmentActivity
) : FragmentStateAdapter(activity) {
    
    override fun createFragment(position: Int): Fragment {
        val step = RegisterStep.entries[position]
        return step.createFragment()
    }

    override fun getItemCount(): Int {
        return RegisterStep.entries.size
    }
}
