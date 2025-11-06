package com.example.grabapp.driver.home.history

import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentHistoryBinding
import com.example.grabapp.driver.home.DriverHomeViewModel

class HistoryFragment : BaseFragment<FragmentHistoryBinding, DriverHomeViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentHistoryBinding> =
        lazy { FragmentHistoryBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverHomeViewModel> =
        lazy { DriverHomeViewModel(requireActivity().application) }

    override fun setUpClick() = Unit
}
