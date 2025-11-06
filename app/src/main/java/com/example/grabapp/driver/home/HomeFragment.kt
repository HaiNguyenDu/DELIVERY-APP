package com.example.grabapp.driver.home

import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentHomeBinding

class HomeFragment : BaseFragment<FragmentHomeBinding, DriverHomeViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentHomeBinding> =
        lazy { FragmentHomeBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverHomeViewModel> =
        lazy { DriverHomeViewModel(requireActivity().application) }

    override fun setUpClick() = Unit
}
