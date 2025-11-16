package com.example.grabapp.driver.register.identification

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
import com.example.grabapp.databinding.FragmentIdentificationCardBinding
import com.example.grabapp.driver.register.RegisterViewModel
import com.example.grabapp.driver.register.bottom_sheet.GenderBottomSheet
import com.example.grabapp.driver.register.bottom_sheet.ProvinceBottomSheet
import com.example.grabapp.extention.onClickWithScale
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class IdentificationCardFragment :
    BaseFragment<FragmentIdentificationCardBinding, RegisterViewModel>() {

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

    override fun getLazyBinding(): Lazy<FragmentIdentificationCardBinding> =
        lazy { FragmentIdentificationCardBinding.inflate(layoutInflater) }

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

            tvExpirationDate.onClickWithScale {
                val tomorrow = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                showDatePicker(
                    minDate = tomorrow,
                    onDateSelected = { date ->
                        if (validateAndSetExpirationDate(date)) {
                            binding.tvExpirationDate.text = dateFormat.format(date)
                            updateValidation()
                        }
                    }
                )
            }

            tvBirth.onClickWithScale {
                showDatePicker(
                    maxDate = System.currentTimeMillis(),
                    onDateSelected = { date ->
                        if (validateAndSetBirthDate(date)) {
                            binding.tvBirth.text = dateFormat.format(date)
                            updateValidation()
                        }
                    }
                )
            }

            tvIssuePlace.onClickWithScale {
                showProvincePicker { selectedProvince ->
                    viewModel.identificationCard.issuePlace = selectedProvince
                    binding.tvIssuePlace.text = selectedProvince
                    updateValidation()
                }
            }

            tvProvince.onClickWithScale {
                showProvincePicker { selectedProvince ->
                    viewModel.identificationCard.province = selectedProvince
                    binding.tvProvince.text = selectedProvince
                    updateValidation()
                }
            }

            tvGender.onClickWithScale {
                showGenderPicker { selectedGender ->
                    viewModel.identificationCard.gender = selectedGender
                    binding.tvGender.text = selectedGender
                    updateValidation()
                }
            }
        }

        binding.edtIDNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                viewModel.identificationCard.idNumber = s?.toString() ?: ""
                validateIdNumber()
                updateValidation()
            }
        })

        observeValidation()
    }

    private fun validateIdNumber() {
        val isValid = viewModel.identificationCard.isValidIdNumber()
        if (viewModel.identificationCard.idNumber.isNotEmpty() && !isValid) {
            binding.edtIDNumber.error = "Số CMND/CCCD phải có 8-12 chữ số"
        } else {
            binding.edtIDNumber.error = null
        }
    }

    private fun validateAndSetBirthDate(date: Long): Boolean {
        val card = viewModel.identificationCard
        val tempBirthDate = card.birthDate

        // Set temporarily to validate
        card.birthDate = date

        if (!card.validateBirthDate()) {
            // Restore original value
            card.birthDate = tempBirthDate
            Toast.makeText(
                requireContext(),
                "Ngày sinh phải trước ngày hiện tại và trên 18 tuổi",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }

    private fun validateAndSetIssueDate(date: Long): Boolean {
        val card = viewModel.identificationCard
        val tempIssueDate = card.issueDate

        // Set temporarily to validate
        card.issueDate = date

        if (!card.validateIssueDate()) {
            // Restore original value
            card.issueDate = tempIssueDate
            val errorMessage = if (date >= System.currentTimeMillis()) {
                "Ngày cấp phải trước ngày hiện tại"
            } else if (card.birthDate != null && date <= card.birthDate!!) {
                "Ngày cấp phải sau ngày sinh"
            } else {
                "Ngày cấp không hợp lệ"
            }
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun validateAndSetExpirationDate(date: Long): Boolean {
        val card = viewModel.identificationCard
        val tempExpirationDate = card.expirationDate

        // Set temporarily to validate
        card.expirationDate = date

        if (!card.validateExpirationDate()) {
            // Restore original value
            card.expirationDate = tempExpirationDate
            val errorMessage = if (date <= System.currentTimeMillis()) {
                "Ngày hết hạn phải sau ngày hiện tại"
            } else if (card.issueDate != null && date <= card.issueDate!!) {
                "Ngày hết hạn phải sau ngày cấp"
            } else {
                "Ngày hết hạn không hợp lệ"
            }
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun updateValidation() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateIdentificationCardValidation()
        }
    }

    private fun observeValidation() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isIdentificationCardValid.collect { isValid ->
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
                    viewModel.identificationCard.frontCardBitmap = it
                    binding.ivFrontCard.setImageBitmap(it)
                    binding.ivFrontCard.setPadding(0, 0, 0, 0)
                } else {
                    viewModel.identificationCard.backCardBitmap = it
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

    private fun showProvincePicker(onProvinceSelected: (String) -> Unit) {
        ProvinceBottomSheet(
            onProvinceSelected = onProvinceSelected
        ).show(parentFragmentManager)
    }

    private fun showGenderPicker(onGenderSelected: (String) -> Unit) {
        GenderBottomSheet(
            onGenderSelected = onGenderSelected
        ).show(parentFragmentManager)
    }
}
