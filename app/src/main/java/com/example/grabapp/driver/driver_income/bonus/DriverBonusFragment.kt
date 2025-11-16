package com.example.grabapp.driver.driver_income.bonus

import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentDriverBonusBinding
import com.example.grabapp.driver.driver_income.DriverIncomeViewModel

class DriverBonusFragment : BaseFragment<FragmentDriverBonusBinding, DriverIncomeViewModel>() {

    override fun getLazyBinding(): Lazy<FragmentDriverBonusBinding> =
        lazy { FragmentDriverBonusBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverIncomeViewModel> =
        lazy { DriverIncomeViewModel(requireActivity().application) }

    override fun setUpClick() = Unit

}
