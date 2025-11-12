package com.example.grabapp.driver.home.data

import androidx.fragment.app.Fragment
import com.example.grabapp.driver.home.HomeFragment
import com.example.grabapp.driver.home.history.HistoryFragment
import com.example.grabapp.driver.home.message.MessageFragment
import com.example.grabapp.driver.home.profile.DriverProfileFragment
import com.example.grabapp.driver.home.wallet.WalletFragment

enum class TabType(
    val subFragment: () -> Fragment,
) {
    HOME(subFragment = { HomeFragment() }),
    HISTORY(subFragment = { HistoryFragment() }),
    MESSAGE(subFragment = { MessageFragment() }),
    WALLET(subFragment = { WalletFragment() }),
    PROFILE(subFragment = { DriverProfileFragment() });
}
