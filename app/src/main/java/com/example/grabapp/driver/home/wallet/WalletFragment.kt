package com.example.grabapp.driver.home.wallet

import android.view.View
import androidx.core.graphics.Insets
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentWalletBinding
import com.example.grabapp.driver.home.DriverHomeViewModel

class WalletFragment : BaseFragment<FragmentWalletBinding, DriverHomeViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentWalletBinding> =
        lazy { FragmentWalletBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverHomeViewModel> =
        lazy { DriverHomeViewModel(requireActivity().application) }

    override fun setUpClick() = Unit

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        binding.emptyView.setPadding(0, inset.top, 0, 0)
        binding.ctlTopBar.setPadding(0, inset.top / 2, 0, 0)
    }
}
