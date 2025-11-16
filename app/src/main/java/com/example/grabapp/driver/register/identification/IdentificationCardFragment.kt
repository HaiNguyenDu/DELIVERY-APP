package com.example.grabapp.driver.register.identification

import android.view.View
import androidx.core.graphics.Insets
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentIdentificationCardBinding
import com.example.grabapp.driver.register.RegisterViewModel

class IdentificationCardFragment : BaseFragment<FragmentIdentificationCardBinding, RegisterViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentIdentificationCardBinding> =
        lazy { FragmentIdentificationCardBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<RegisterViewModel> =
        lazy { RegisterViewModel(requireActivity().application) }

    override fun setUpClick() = Unit

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        view.setPadding(0, 0, 0, 0)
    }
}
