package com.example.grabapp.driver.register.emergency_contact

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.graphics.Insets
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentEmergencyContactBinding
import com.example.grabapp.driver.register.RegisterViewModel
import com.example.grabapp.driver.register.bottom_sheet.ProvinceBottomSheet
import com.example.grabapp.driver.register.bottom_sheet.RelationshipBottomSheet
import com.example.grabapp.extention.onClickWithScale
import kotlinx.coroutines.launch

class EmergencyContactFragment :
    BaseFragment<FragmentEmergencyContactBinding, RegisterViewModel>() {
    
    override fun getLazyBinding(): Lazy<FragmentEmergencyContactBinding> =
        lazy { FragmentEmergencyContactBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<RegisterViewModel> =
        lazy { ViewModelProvider(requireActivity())[RegisterViewModel::class.java] }

    override fun setUpClick() {
        binding.apply {
            edtEmergencyName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    viewModel.emergencyContact.name = s?.toString() ?: ""
                    updateValidation()
                }
            })

            tvRelationship.onClickWithScale {
                showRelationshipPicker { selectedRelationship ->
                    viewModel.emergencyContact.relationship = selectedRelationship
                    binding.tvRelationship.text = selectedRelationship
                    updateValidation()
                }
            }

            edtEmergencyPhone.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val text = s?.toString() ?: ""
                    if (text.length > 10) {
                        val truncated = text.take(10)
                        binding.edtEmergencyPhone.setText(truncated)
                        binding.edtEmergencyPhone.setSelection(truncated.length)
                    } else {
                        viewModel.emergencyContact.phone = text
                        validatePhone()
                        updateValidation()
                    }
                }
            })

            edtHouseAddress.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    viewModel.emergencyContact.houseAddress = s?.toString() ?: ""
                    updateValidation()
                }
            })

            edtCommune.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    viewModel.emergencyContact.commune = s?.toString() ?: ""
                    updateValidation()
                }
            })

            tvProvince.onClickWithScale {
                showProvincePicker { selectedProvince ->
                    viewModel.emergencyContact.province = selectedProvince
                    binding.tvProvince.text = selectedProvince
                    updateValidation()
                }
            }
        }

        observeValidation()
    }

    private fun validatePhone() {
        val isValid = viewModel.emergencyContact.isValidPhone()
        if (viewModel.emergencyContact.phone.isNotEmpty() && !isValid) {
            binding.edtEmergencyPhone.error = "Số điện thoại không được quá 10 ký tự"
        } else {
            binding.edtEmergencyPhone.error = null
        }
    }

    private fun updateValidation() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateEmergencyContactValidation()
        }
    }

    private fun observeValidation() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isEmergencyContactValid.collect { isValid ->
                val activity =
                    requireActivity() as? com.example.grabapp.driver.register.RegisterActivity
                activity?.updateNextButtonState(isValid)
            }
        }
    }

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        view.setPadding(0, 0, 0, 0)
    }

    private fun showRelationshipPicker(onRelationshipSelected: (String) -> Unit) {
        RelationshipBottomSheet(
            onRelationshipSelected = onRelationshipSelected
        ).show(parentFragmentManager)
    }

    private fun showProvincePicker(onProvinceSelected: (String) -> Unit) {
        ProvinceBottomSheet(
            onProvinceSelected = onProvinceSelected
        ).show(parentFragmentManager)
    }
}
