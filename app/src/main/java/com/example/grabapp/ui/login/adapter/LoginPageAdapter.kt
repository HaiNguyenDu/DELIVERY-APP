package com.example.grabapp.ui.login.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.grabapp.ui.login.fragment.ConfirmOTPFragment
import com.example.grabapp.ui.login.fragment.EnterNameFragment
import com.example.grabapp.ui.login.fragment.MainLoginFragment
import com.example.grabapp.ui.login.fragment.SignInFragment

class LoginPageAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    companion object {
        const val FRAGMENT_LOGIN = 0
        const val FRAGMENT_SIGN_IN = 1
        const val FRAGMENT_CONFIRM = 2
        const val FRAGMENT_ENTER_NAME = 3
        const val LOGIN_FRAGMENT_COUNT = 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            FRAGMENT_LOGIN -> MainLoginFragment()
            FRAGMENT_SIGN_IN -> SignInFragment()
            FRAGMENT_CONFIRM -> ConfirmOTPFragment()
            FRAGMENT_ENTER_NAME -> EnterNameFragment()
            else -> MainLoginFragment()
        }
    }

    override fun getItemCount() = LOGIN_FRAGMENT_COUNT
}