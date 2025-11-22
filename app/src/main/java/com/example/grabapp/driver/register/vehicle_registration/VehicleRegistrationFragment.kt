package com.example.grabapp.driver.register.vehicle_registration

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
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentVehicleRegistrationBinding
import com.example.grabapp.driver.register.RegisterViewModel
import com.example.grabapp.driver.register.bottom_sheet.FuelBottomSheet
import com.example.grabapp.driver.register.bottom_sheet.VehicleBrandBottomSheet
import com.example.grabapp.extention.onClickWithScale
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.io.InputStream
import java.util.Calendar

class VehicleRegistrationFragment : BaseFragment<FragmentVehicleRegistrationBinding, RegisterViewModel>() {

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

    override fun getLazyBinding(): Lazy<FragmentVehicleRegistrationBinding> =
        lazy { FragmentVehicleRegistrationBinding.inflate(layoutInflater) }

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

            edtLicensePlate.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val text = s?.toString() ?: ""
                    val filteredText = text.filter { it.isDigit() || it.isUpperCase() }
                    if (text != filteredText) {
                        binding.edtLicensePlate.setText(filteredText)
                        binding.edtLicensePlate.setSelection(filteredText.length)
                    } else {
                        viewModel.vehicleRegistration.licensePlate = filteredText
                        validateLicensePlate()
                    }
                }
            })

            tvFuelType.onClickWithScale {
                showFuelPicker { selectedFuel ->
                    viewModel.vehicleRegistration.fuelType = selectedFuel
                    binding.tvFuelType.text = selectedFuel
                }
            }

            tvVehicleBrand.onClickWithScale {
                showVehicleBrandPicker { selectedBrand ->
                    viewModel.vehicleRegistration.vehicleBrand = selectedBrand
                    binding.tvVehicleBrand.text = selectedBrand
                }
            }

            edtVehicleModel.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    viewModel.vehicleRegistration.vehicleModel = s?.toString() ?: ""
                }
            })

            edtManufactureYear.setOnClickListener {
                showYearPicker { year ->
                    if (validateAndSetManufactureYear(year)) {
                        binding.edtManufactureYear.setText(year.toString())
                    }
                }
            }
        }
    }

    private fun validateLicensePlate() {
        val isValid = viewModel.vehicleRegistration.isValidLicensePlate()
        if (viewModel.vehicleRegistration.licensePlate.isNotEmpty() && !isValid) {
            binding.edtLicensePlate.error = "Biển số xe chỉ được chứa chữ in hoa hoặc số"
        } else {
            binding.edtLicensePlate.error = null
        }
    }

    private fun validateAndSetManufactureYear(year: Int): Boolean {
        val registration = viewModel.vehicleRegistration
        val tempYear = registration.manufactureYear

        registration.manufactureYear = year

        if (!registration.isValidManufactureYear()) {
            registration.manufactureYear = tempYear
            Toast.makeText(
                requireContext(),
                "Năm sản xuất phải trước năm hiện tại",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
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
                    viewModel.vehicleRegistration.frontCardBitmap = it
                    binding.ivFrontCard.setImageBitmap(it)
                    binding.ivFrontCard.setPadding(0, 0, 0, 0)
                } else {
                    viewModel.vehicleRegistration.backCardBitmap = it
                    binding.ivBackCard.setImageBitmap(it)
                    binding.ivBackCard.setPadding(0, 0, 0, 0)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showYearPicker(onYearSelected: (Int) -> Unit) {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, 1900)
        calendar.set(Calendar.MONTH, 0)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val minDate = calendar.timeInMillis

        calendar.set(Calendar.YEAR, currentYear - 1)
        calendar.set(Calendar.MONTH, 11)
        calendar.set(Calendar.DAY_OF_MONTH, 31)
        val maxDate = calendar.timeInMillis

        val constraintsBuilder = CalendarConstraints.Builder()
            .setStart(minDate)
            .setEnd(maxDate)

        val builder = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(constraintsBuilder.build())
            .setTitleText("Chọn năm sản xuất")

        val picker = builder.build()
        picker.addOnPositiveButtonClickListener { selectedDate ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selectedDate
            val year = calendar.get(Calendar.YEAR)
            onYearSelected(year)
        }

        picker.show(parentFragmentManager, "YearPicker")
    }

    private fun showFuelPicker(onFuelSelected: (String) -> Unit) {
        FuelBottomSheet(
            onFuelSelected = onFuelSelected
        ).show(parentFragmentManager)
    }

    private fun showVehicleBrandPicker(onBrandSelected: (String) -> Unit) {
        VehicleBrandBottomSheet(
            onBrandSelected = onBrandSelected
        ).show(parentFragmentManager)
    }
}
