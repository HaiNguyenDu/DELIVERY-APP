package com.example.grabapp.driver.register.avatar

import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentAvatarBinding
import com.example.grabapp.driver.register.RegisterViewModel

class AvatarFragment : BaseFragment<FragmentAvatarBinding, RegisterViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentAvatarBinding> =
        lazy { FragmentAvatarBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<RegisterViewModel> =
        lazy { RegisterViewModel(requireActivity().application) }

    override fun setUpClick() = Unit
}
