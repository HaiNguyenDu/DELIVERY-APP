package com.example.grabapp.driver.register.emergency_contact

import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentEmergencyContactBinding
import com.example.grabapp.driver.register.RegisterViewModel

class EmergencyContactFragment :
    BaseFragment<FragmentEmergencyContactBinding, RegisterViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentEmergencyContactBinding> =
        lazy { FragmentEmergencyContactBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<RegisterViewModel> =
        lazy { RegisterViewModel(requireActivity().application) }

    override fun setUpClick() = Unit
}
