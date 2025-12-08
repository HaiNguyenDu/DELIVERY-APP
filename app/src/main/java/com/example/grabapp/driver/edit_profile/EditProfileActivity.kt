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
import com.example.grabapp.data.TokenStorage
import com.example.grabapp.databinding.ActivityEditProfileBinding
import com.example.grabapp.driver.register.bottom_sheet.GenderBottomSheet
import com.example.grabapp.driver.register.bottom_sheet.ProvinceBottomSheet
import com.example.grabapp.extention.onClickWithScale
import com.example.grabapp.data.model.DriverProfile
import com.example.grabapp.data.repository.DriverRepository
import com.example.grabapp.driver.base.BaseDriverActivity
import com.example.grabapp.model.CCCDInfo
import com.example.grabapp.model.DriverStatus
import com.example.grabapp.model.Transportation
import com.example.grabapp.view.bottom_sheet.TransportationBottomSheet
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditProfileActivity : BaseDriverActivity<ActivityEditProfileBinding, EditProfileViewModel>() {

    private val tokenStorage by lazy { TokenStorage(applicationContext) }
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
        loadSavedPhone()
        // Ưu tiên luồng get driver info từ server, nếu lỗi sẽ fallback sang luồng register
        viewModel.fetchDriverInfo()
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

        lifecycleScope.launch {
            viewModel.registerResult.collect { result ->
                result?.let {
                    when (it) {
                        is DriverRepository.RegisterResult.Success -> {
                            Toast.makeText(
                                this@EditProfileActivity,
                                "Gửi yêu cầu thành công",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.tvSubmit.text = "Chờ kiểm duyệt"
                            disableAllFields()
                        }

                        is DriverRepository.RegisterResult.Error -> {
                            Toast.makeText(
                                this@EditProfileActivity,
                                "Lỗi: ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.driverInfoState.collect { state ->
                when (state) {
                    is EditProfileViewModel.DriverInfoState.Existing -> {
                        fillProfileData(state.profile)
                        applyDriverInfoUi(state.status)
                    }

                    is EditProfileViewModel.DriverInfoState.NotFound -> {
                        Toast.makeText(
                            this@EditProfileActivity,
                            "Không tìm thấy thông tin tài xế, vui lòng đăng ký.",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.tvSubmit.visibility = View.VISIBLE
                        loadSavedProfile()
                    }

                    is EditProfileViewModel.DriverInfoState.Error -> {
                        Toast.makeText(
                            this@EditProfileActivity,
                            state.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.tvSubmit.visibility = View.VISIBLE
                        loadSavedProfile()
                    }

                    null -> {
                        // ignore
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            flBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
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
                    viewModel.registerDriver(
                        vehiclePlateNumber = binding.edtVehiclePlate.text?.toString()?.trim() ?: "",
                        licenseNumber = binding.edtLicenseNumber.text?.toString()?.trim() ?: "",
                        identityFullName = binding.edtName.text?.toString()?.trim() ?: "",
                        identityNumber = binding.edtCCCD.text?.toString()?.trim() ?: "",
                        identityIssueDate = selectedIssueDate!!,
                        identityIssuePlace = selectedIssuePlace!!,
                        identityAddress = binding.edtAddress.text?.toString()?.trim() ?: "",
                        identityGender = selectedGender!!,
                        identityBirthdate = selectedBirthDate!!,
                        vehicleType = selectedVehicleType!!.transportationName
                    )
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

    private fun loadSavedPhone() {
        val savedPhone = tokenStorage.getPhone()
        savedPhone?.let {
            binding.edtPhone.setText(it)
        }
    }

    private fun loadSavedProfile() {
        val savedProfile = viewModel.getSavedProfile()
        if (viewModel.isPending()) {
            savedProfile?.let {
                fillProfileData(it)
                binding.tvSubmit.text = "Chờ kiểm duyệt"
                disableAllFields()
            }
        } else {
            savedProfile?.let {
                fillProfileData(it)
            }
        }
    }

    private fun fillProfileData(profile: DriverProfile) {
        binding.apply {
            edtName.setText(profile.name)
            edtCCCD.setText(profile.cccdNumber)
            edtLicenseNumber.setText(profile.licenseNumber)
            edtAddress.setText(profile.address)
            edtVehiclePlate.setText(profile.vehiclePlate)

            selectedBirthDate = profile.birthDate
            tvBirth.text = dateFormat.format(profile.birthDate)

            selectedGender = profile.gender
            tvGender.text = profile.gender

            selectedIssueDate = profile.issueDate
            tvIssueDate.text = dateFormat.format(profile.issueDate)

            selectedIssuePlace = profile.issuePlace
            tvIssuePlace.text = profile.issuePlace

            val vehicleType = try {
                Transportation.entries.firstOrNull {
                    it.transportationName == profile.vehicleType
                } ?: Transportation.GRAB_BIKE
            } catch (e: Exception) {
                Transportation.GRAB_BIKE
            }
            selectedVehicleType = vehicleType
            tvVehicleType.text = getString(vehicleType.stringResId)
        }
        updateSubmitButtonState()
    }

    private fun applyDriverInfoUi(status: DriverStatus) {
        binding.apply {
            constrainLayout5.visibility = View.GONE
            tvSubmit.visibility = View.GONE
            constrainLayout7.visibility =
                if (status == DriverStatus.PENDING) View.VISIBLE else View.GONE

            edtName.isEnabled = false
            edtCCCD.isEnabled = false
            edtLicenseNumber.isEnabled = false
            edtAddress.isEnabled = false
            edtVehiclePlate.isEnabled = false
            edtPhone.isEnabled = false
            tvBirth.isEnabled = false
            tvGender.isEnabled = false
            tvIssueDate.isEnabled = false
            tvIssuePlace.isEnabled = false
            ivUploadAvatar.isEnabled = false
            ivAvatar.isEnabled = false
            ivFrontCard.isEnabled = false
            ivBackCard.isEnabled = false
        }
    }

    private fun disableAllFields() {
        binding.apply {
            edtName.isEnabled = false
            edtCCCD.isEnabled = false
            edtLicenseNumber.isEnabled = false
            edtAddress.isEnabled = false
            edtVehiclePlate.isEnabled = false
            edtPhone.isEnabled = false

            tvBirth.isEnabled = false
            tvGender.isEnabled = false
            tvIssueDate.isEnabled = false
            tvIssuePlace.isEnabled = false
            tvVehicleType.isEnabled = false

            ivUploadAvatar.isEnabled = false
            ivAvatar.isEnabled = false
            ivFrontCard.isEnabled = false
            ivBackCard.isEnabled = false

            tvSubmit.isEnabled = false
        }
    }

    private enum class ImageType {
        AVATAR,
        FRONT_CARD,
        BACK_CARD
    }
}
