package com.example.grabapp.driver.register.avatar

import android.view.View
import androidx.core.graphics.Insets
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentAvatarBinding
import com.example.grabapp.driver.register.RegisterViewModel

class AvatarFragment : BaseFragment<FragmentAvatarBinding, RegisterViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentAvatarBinding> =
        lazy { FragmentAvatarBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<RegisterViewModel> =
        lazy { RegisterViewModel(requireActivity().application) }

    override fun setUpClick() = Unit

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        view.setPadding(0, 0, 0, 0)
    }
}
