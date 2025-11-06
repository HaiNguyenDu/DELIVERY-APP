package com.example.grabapp.driver.home.profile

import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentDriverProfileBinding
import com.example.grabapp.driver.home.DriverHomeViewModel

class DriverProfileFragment : BaseFragment<FragmentDriverProfileBinding, DriverHomeViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentDriverProfileBinding> =
        lazy { FragmentDriverProfileBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverHomeViewModel> =
        lazy { DriverHomeViewModel(requireActivity().application) }

    override fun setUpClick() = Unit
}
