package com.example.grabapp.driver.register.driving_license

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.Insets
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentDrivingLicenseBinding
import com.example.grabapp.driver.register.RegisterViewModel
import com.example.grabapp.driver.register.bottom_sheet.DrivingLicenseBottomSheet
import com.example.grabapp.extention.onClickWithScale
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale

class DrivingLicenseFragment : BaseFragment<FragmentDrivingLicenseBinding, RegisterViewModel>() {
    
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private val pickFrontCardLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { loadImageFromUri(it, true) }
    }

    private val pickBackCardLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { loadImageFromUri(it, false) }
    }

    override fun getLazyBinding(): Lazy<FragmentDrivingLicenseBinding> =
        lazy { FragmentDrivingLicenseBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<RegisterViewModel> =
        lazy { ViewModelProvider(requireActivity())[RegisterViewModel::class.java] }

    override fun setUpClick() {
        binding.apply {
            ivFrontCard.onClickWithScale {
                openImagePicker(true)
            }

            ivBackCard.onClickWithScale {
                openImagePicker(false)
            }

            tvIssueDate.onClickWithScale {
                showDatePicker(
                    maxDate = System.currentTimeMillis(),
                    onDateSelected = { date ->
                        if (validateAndSetIssueDate(date)) {
                            binding.tvIssueDate.text = dateFormat.format(date)
                            updateValidation()
                        }
                    }
                )
            }

            tvCategories.onClickWithScale {
                showDrivingLicensePicker { selectedCategory ->
                    viewModel.drivingLicense.category = selectedCategory
                    binding.tvCategories.text = selectedCategory
                    updateValidation()
                }
            }
        }

        binding.edtIDNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s?.toString() ?: ""
                val filteredText = text.filter { it.isDigit() || it.isUpperCase() }
                if (text != filteredText) {
                    binding.edtIDNumber.setText(filteredText)
                    binding.edtIDNumber.setSelection(filteredText.length)
                }
                
                if (filteredText.length > 12) {
                    val truncated = filteredText.take(12)
                    binding.edtIDNumber.setText(truncated)
                    binding.edtIDNumber.setSelection(truncated.length)
                } else {
                    viewModel.drivingLicense.idNumber = filteredText
                    validateIdNumber()
                    updateValidation()
                }
            }
        })

        observeValidation()
    }

    private fun validateIdNumber() {
        val isValid = viewModel.drivingLicense.isValidIdNumber()
        if (viewModel.drivingLicense.idNumber.isNotEmpty() && !isValid) {
            binding.edtIDNumber.error = "Số bằng lái xe chỉ được chứa số hoặc chữ in hoa, tối đa 12 ký tự"
        } else {
            binding.edtIDNumber.error = null
        }
    }

    private fun validateAndSetIssueDate(date: Long): Boolean {
        val license = viewModel.drivingLicense
        val tempIssueDate = license.issueDate

        license.issueDate = date

        if (!license.validateIssueDate()) {
            license.issueDate = tempIssueDate
            Toast.makeText(
                requireContext(),
                "Ngày cấp phải trước ngày hiện tại",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }

    private fun updateValidation() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateDrivingLicenseValidation()
        }
    }

    private fun observeValidation() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isDrivingLicenseValid.collect { isValid ->
                val activity =
                    requireActivity() as? com.example.grabapp.driver.register.RegisterActivity
                activity?.updateNextButtonState(isValid)
            }
        }
    }

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        view.setPadding(0, 0, 0, 0)
    }

    private fun openImagePicker(isFrontCard: Boolean) {
        val request = PickVisualMediaRequest.Builder()
            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
            .build()

        if (isFrontCard) {
            pickFrontCardLauncher.launch(request)
        } else {
            pickBackCardLauncher.launch(request)
        }
    }

    private fun loadImageFromUri(uri: Uri, isFrontCard: Boolean) {
        try {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            bitmap?.let {
                if (isFrontCard) {
                    viewModel.drivingLicense.frontCardBitmap = it
                    binding.ivFrontCard.setImageBitmap(it)
                    binding.ivFrontCard.setPadding(0, 0, 0, 0)
                } else {
                    viewModel.drivingLicense.backCardBitmap = it
                    binding.ivBackCard.setImageBitmap(it)
                    binding.ivBackCard.setPadding(0, 0, 0, 0)
                }
                updateValidation()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showDatePicker(
        minDate: Long? = null,
        maxDate: Long? = null,
        onDateSelected: (Long) -> Unit
    ) {
        val constraintsBuilder = CalendarConstraints.Builder()
        minDate?.let { constraintsBuilder.setStart(it) }
        maxDate?.let { constraintsBuilder.setEnd(it) }

        val builder = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(constraintsBuilder.build())

        val picker = builder.build()
        picker.addOnPositiveButtonClickListener { selectedDate ->
            onDateSelected(selectedDate)
        }

        picker.show(parentFragmentManager, "DatePicker")
    }

    private fun showDrivingLicensePicker(onCategorySelected: (String) -> Unit) {
        DrivingLicenseBottomSheet(
            onCategorySelected = onCategorySelected
        ).show(parentFragmentManager)
    }
}
