package com.example.grabapp.driver.confirm_driver.pager

import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentConfirmPagerBinding
import com.example.grabapp.driver.confirm_driver.ConfirmDriverViewModel

class ConfirmPagerFragment : BaseFragment<FragmentConfirmPagerBinding, ConfirmDriverViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentConfirmPagerBinding> =
        lazy { FragmentConfirmPagerBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<ConfirmDriverViewModel> =
        lazy { ConfirmDriverViewModel(requireActivity().application) }

    override fun setUpClick() = Unit
}
