package com.example.grabapp.driver.register.bank_account

import android.view.View
import androidx.core.graphics.Insets
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentBankAccountBinding
import com.example.grabapp.driver.register.RegisterViewModel

class BankAccountFragment : BaseFragment<FragmentBankAccountBinding, RegisterViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentBankAccountBinding> =
        lazy { FragmentBankAccountBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<RegisterViewModel> =
        lazy { RegisterViewModel(requireActivity().application) }

    override fun setUpClick() = Unit

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        view.setPadding(0, 0, 0, 0)
    }
}
