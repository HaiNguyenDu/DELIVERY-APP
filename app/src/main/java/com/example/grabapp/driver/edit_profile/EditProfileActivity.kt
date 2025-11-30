package com.example.grabapp.driver.edit_profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.Insets
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityEditProfileBinding
import com.example.grabapp.driver.register.bottom_sheet.GenderBottomSheet
import com.example.grabapp.driver.register.bottom_sheet.ProvinceBottomSheet
import com.example.grabapp.extention.onClickWithScale
import com.example.grabapp.model.CCCDInfo
import com.example.grabapp.model.Transportation
import com.example.grabapp.view.bottom_sheet.TransportationBottomSheet
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditProfileActivity : BaseActivity<ActivityEditProfileBinding, EditProfileViewModel>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val dateFormatInput = SimpleDateFormat("ddMMyyyy", Locale.getDefault())

    // Track uploaded images
    private var isAvatarUploaded = false
    private var isFrontCardUploaded = false
    private var isBackCardUploaded = false

    // Store bitmaps for QR scanning
    private var frontCardBitmap: Bitmap? = null
    private var backCardBitmap: Bitmap? = null

    // Track selected values
    private var selectedBirthDate: Long? = null
    private var selectedGender: String? = null
    private var selectedIssueDate: Long? = null
    private var selectedIssuePlace: String? = null
    private var selectedVehicleType: Transportation? = null

    private val pickAvatarLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { loadImageFromUri(it, ImageType.AVATAR) }
    }

    private val pickFrontCardLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { loadImageFromUri(it, ImageType.FRONT_CARD) }
    }

    private val pickBackCardLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { loadImageFromUri(it, ImageType.BACK_CARD) }
    }

    override fun getLazyBinding(): Lazy<ActivityEditProfileBinding> =
        lazy { ActivityEditProfileBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<EditProfileViewModel> =
        lazy { EditProfileViewModel(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupClickListeners()
        setupTextWatchers()
        observeViewModel()
        updateSubmitButtonState()
    }

    override fun handleInsets(v: View, insets: Insets) {
        binding.root.setPadding(0, -insets.top, 0, 0)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.main.isEnabled = !isLoading
            }
        }

        lifecycleScope.launch {
            viewModel.cccdInfo.collect { cccdInfo ->
                cccdInfo?.let {
                    fillFormData(it)
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            ivUploadAvatar.onClickWithScale {
                if (!isScanning()) {
                    openImagePicker(ImageType.AVATAR)
                }
            }
            ivAvatar.onClickWithScale {
                if (!isScanning()) {
                    openImagePicker(ImageType.AVATAR)
                }
            }
            ivFrontCard.onClickWithScale {
                if (!isScanning()) {
                    openImagePicker(ImageType.FRONT_CARD)
                }
            }
            ivBackCard.onClickWithScale {
                if (!isScanning()) {
                    openImagePicker(ImageType.BACK_CARD)
                }
            }

            tvBirth.onClickWithScale {
                showDatePicker(
                    maxDate = System.currentTimeMillis(),
                    onDateSelected = { date ->
                        if (date < System.currentTimeMillis()) {
                            selectedBirthDate = date
                            binding.tvBirth.text = dateFormat.format(date)
                            updateSubmitButtonState()
                        } else {
                            Toast.makeText(
                                this@EditProfileActivity,
                                "Ngày sinh phải trong quá khứ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            }

            tvIssueDate.onClickWithScale {
                showDatePicker(
                    maxDate = System.currentTimeMillis(),
                    onDateSelected = { date ->
                        if (date < System.currentTimeMillis()) {
                            selectedIssueDate = date
                            binding.tvIssueDate.text = dateFormat.format(date)
                            updateSubmitButtonState()
                        } else {
                            Toast.makeText(
                                this@EditProfileActivity,
                                "Ngày cấp phải trong quá khứ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            }

            tvGender.onClickWithScale {
                showGenderPicker { selectedGender ->
                    this@EditProfileActivity.selectedGender = selectedGender
                    binding.tvGender.text = selectedGender
                    updateSubmitButtonState()
                }
            }

            tvIssuePlace.onClickWithScale {
                showProvincePicker { selectedProvince ->
                    selectedIssuePlace = selectedProvince
                    binding.tvIssuePlace.text = selectedProvince
                    updateSubmitButtonState()
                }
            }

            tvVehicleType.onClickWithScale {
                showTransportationPicker(
                    selectedTransportation = selectedVehicleType ?: Transportation.GRAB_BIKE
                ) { transportation ->
                    selectedVehicleType = transportation
                    binding.tvVehicleType.text = getString(transportation.stringResId)
                    updateSubmitButtonState()
                }
            }

            tvSubmit.onClickWithScale {
                if (isAllFieldsFilled()) {
                    Toast.makeText(
                        this@EditProfileActivity,
                        "Gửi yêu cầu thành công",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@EditProfileActivity,
                        "Vui lòng điền đầy đủ thông tin!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupTextWatchers() {
        binding.apply {
            edtName.addTextChangedListener(createTextWatcher { updateSubmitButtonState() })
            edtCCCD.addTextChangedListener(createTextWatcher { updateSubmitButtonState() })
            edtLicenseNumber.addTextChangedListener(createTextWatcher { updateSubmitButtonState() })
            edtAddress.addTextChangedListener(createTextWatcher { updateSubmitButtonState() })
            edtVehiclePlate.addTextChangedListener(createTextWatcher { updateSubmitButtonState() })
        }
    }

    private fun createTextWatcher(onTextChanged: () -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                onTextChanged()
            }
        }
    }

    private fun openImagePicker(imageType: ImageType) {
        val request = PickVisualMediaRequest.Builder()
            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
            .build()

        when (imageType) {
            ImageType.AVATAR -> pickAvatarLauncher.launch(request)
            ImageType.FRONT_CARD -> pickFrontCardLauncher.launch(request)
            ImageType.BACK_CARD -> pickBackCardLauncher.launch(request)
        }
    }

    private fun loadImageFromUri(uri: Uri, imageType: ImageType) {
        val requestOption = RequestOptions()
            .override(
                when (imageType) {
                    ImageType.AVATAR -> 512
                    else -> 1200
                }
            )
            .centerCrop()

        when (imageType) {
            ImageType.AVATAR -> {
                Glide.with(this)
                    .load(uri)
                    .apply(requestOption.circleCrop())
                    .into(binding.ivAvatar)
                isAvatarUploaded = true
            }

            ImageType.FRONT_CARD -> {
                Glide.with(this)
                    .load(uri)
                    .apply(requestOption)
                    .into(binding.ivFrontCard)
                binding.ivFrontCard.setPadding(0, 0, 0, 0)
                isFrontCardUploaded = true

                frontCardBitmap = getBitmapFromUri(uri)
                checkAndScanBarcode()
                checkAndShowUploadDate()
            }

            ImageType.BACK_CARD -> {
                Glide.with(this)
                    .load(uri)
                    .apply(requestOption)
                    .into(binding.ivBackCard)
                binding.ivBackCard.setPadding(0, 0, 0, 0)
                isBackCardUploaded = true

                backCardBitmap = getBitmapFromUri(uri)
                checkAndScanBarcode()
                checkAndShowUploadDate()
            }
        }
        updateSubmitButtonState()
    }

    private fun isScanning(): Boolean {
        return viewModel.isLoading.value
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun checkAndScanBarcode() {
        if (frontCardBitmap != null && backCardBitmap != null) {
            viewModel.scanBarcodesFromBitmaps(frontCardBitmap!!, backCardBitmap!!)
        }
    }

    private fun fillFormData(cccdInfo: CCCDInfo) {
        binding.apply {
            edtName.setText(cccdInfo.fullName)
            edtCCCD.setText(cccdInfo.cccdNumber)
            selectedGender = cccdInfo.gender
            tvGender.text = cccdInfo.gender
            edtAddress.setText(cccdInfo.address)

            try {
                val birthDate = dateFormatInput.parse(cccdInfo.dateOfBirth)
                birthDate?.let {
                    selectedBirthDate = it.time
                    tvBirth.text = dateFormat.format(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        updateSubmitButtonState()
    }

    private fun checkAndShowUploadDate() {
        if (isFrontCardUploaded && isBackCardUploaded) {
            val currentDate = Calendar.getInstance().time
            binding.tvUploadedDate.text = "Tải lên: ${dateFormat.format(currentDate)}"
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

        picker.show(supportFragmentManager, "DatePicker")
    }

    private fun showGenderPicker(onGenderSelected: (String) -> Unit) {
        GenderBottomSheet(
            onGenderSelected = onGenderSelected
        ).show(supportFragmentManager, "GenderBottomSheet")
    }

    private fun showProvincePicker(onProvinceSelected: (String) -> Unit) {
        ProvinceBottomSheet(
            onProvinceSelected = onProvinceSelected
        ).show(supportFragmentManager, "ProvinceBottomSheet")
    }

    private fun showTransportationPicker(
        selectedTransportation: Transportation,
        onTransportationSelected: (Transportation) -> Unit
    ) {
        TransportationBottomSheet(
            selectedTransportation = selectedTransportation,
            onTransportationSelected = onTransportationSelected
        ).show(supportFragmentManager, "TransportationBottomSheet")
    }

    private fun isAllFieldsFilled(): Boolean {
        return isAvatarUploaded &&
                isFrontCardUploaded &&
                isBackCardUploaded &&
                binding.edtName.text?.toString()?.trim()?.isNotEmpty() == true &&
                binding.edtCCCD.text?.toString()?.trim()?.isNotEmpty() == true &&
                binding.edtLicenseNumber.text?.toString()?.trim()?.isNotEmpty() == true &&
                binding.edtAddress.text?.toString()?.trim()?.isNotEmpty() == true &&
                binding.edtVehiclePlate.text?.toString()?.trim()?.isNotEmpty() == true &&
                selectedBirthDate != null &&
                selectedGender != null &&
                selectedIssueDate != null &&
                selectedIssuePlace != null &&
                selectedVehicleType != null
    }

    private fun updateSubmitButtonState() {
        val isFilled = isAllFieldsFilled()
        binding.tvSubmit.alpha = if (isFilled) 1f else 0.5f
        binding.tvSubmit.isEnabled = isFilled
    }

    private enum class ImageType {
        AVATAR,
        FRONT_CARD,
        BACK_CARD
    }
}
