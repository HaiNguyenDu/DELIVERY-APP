package com.example.grabapp.driver.driver_income.wallet

import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentDriverWalletBinding
import com.example.grabapp.driver.driver_income.DriverIncomeViewModel

class DriverWalletFragment : BaseFragment<FragmentDriverWalletBinding, DriverIncomeViewModel>() {

    override fun getLazyBinding(): Lazy<FragmentDriverWalletBinding> =
        lazy { FragmentDriverWalletBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverIncomeViewModel> =
        lazy { DriverIncomeViewModel(requireActivity().application) }

    override fun setUpClick() = Unit
}
