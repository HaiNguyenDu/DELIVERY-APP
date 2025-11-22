package com.example.grabapp.driver.register.bank_account

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.graphics.Insets
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentBankAccountBinding
import com.example.grabapp.driver.register.RegisterViewModel
import com.example.grabapp.driver.register.bottom_sheet.BankBottomSheet
import com.example.grabapp.extention.onClickWithScale
import kotlinx.coroutines.launch

class BankAccountFragment : BaseFragment<FragmentBankAccountBinding, RegisterViewModel>() {
    
    override fun getLazyBinding(): Lazy<FragmentBankAccountBinding> =
        lazy { FragmentBankAccountBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<RegisterViewModel> =
        lazy { ViewModelProvider(requireActivity())[RegisterViewModel::class.java] }

    override fun setUpClick() {
        binding.apply {
            edtAccountName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val text = s?.toString() ?: ""
                    val filteredText = text.filter { char ->
                        char.isLetter() || char.isWhitespace()
                    }
                    if (text != filteredText) {
                        binding.edtAccountName.setText(filteredText)
                        binding.edtAccountName.setSelection(filteredText.length)
                    } else {
                        viewModel.bankAccount.accountName = filteredText
                        validateAccountName()
                        updateValidation()
                    }
                }
            })

            edtAccountNumber.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val text = s?.toString() ?: ""
                    val filteredText = text.filter { it.isDigit() }
                    if (text != filteredText) {
                        binding.edtAccountNumber.setText(filteredText)
                        binding.edtAccountNumber.setSelection(filteredText.length)
                    } else {
                        viewModel.bankAccount.accountNumber = filteredText
                        validateAccountNumber()
                        updateValidation()
                    }
                }
            })

            tvBankName.onClickWithScale {
                showBankPicker { selectedBank ->
                    viewModel.bankAccount.bankName = selectedBank
                    binding.tvBankName.text = selectedBank
                    updateValidation()
                }
            }

            cbConfirm.setOnCheckedChangeListener { _, isChecked ->
                viewModel.bankAccount.isConfirmed = isChecked
                updateValidation()
            }
        }

        observeValidation()
    }

    private fun validateAccountName() {
        val isValid = viewModel.bankAccount.isValidAccountName()
        if (viewModel.bankAccount.accountName.isNotEmpty() && !isValid) {
            binding.edtAccountName.error = "Tên tài khoản chỉ được chứa chữ cái"
        } else {
            binding.edtAccountName.error = null
        }
    }

    private fun validateAccountNumber() {
        val isValid = viewModel.bankAccount.isValidAccountNumber()
        if (viewModel.bankAccount.accountNumber.isNotEmpty() && !isValid) {
            binding.edtAccountNumber.error = "Số tài khoản phải là số và nhiều hơn 9 chữ số"
        } else {
            binding.edtAccountNumber.error = null
        }
    }

    private fun updateValidation() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateBankAccountValidation()
        }
    }

    private fun observeValidation() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isBankAccountValid.collect { isValid ->
                val activity =
                    requireActivity() as? com.example.grabapp.driver.register.RegisterActivity
                activity?.updateNextButtonState(isValid)
            }
        }
    }

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        view.setPadding(0, 0, 0, 0)
    }

    private fun showBankPicker(onBankSelected: (String) -> Unit) {
        BankBottomSheet(
            onBankSelected = onBankSelected
        ).show(parentFragmentManager)
    }
}
