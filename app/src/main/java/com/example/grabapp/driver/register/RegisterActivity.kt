package com.example.grabapp.driver.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.graphics.Insets
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.grabapp.R
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityRegisterBinding
import com.example.grabapp.driver.profile_preview.ProfileReviewActivity
import com.example.grabapp.driver.register.adapter.RegisterPageAdapter
import com.example.grabapp.driver.register.data.RegisterStep
import com.example.grabapp.extention.onClickWithScale
import com.example.grabapp.extention.startActivity
import com.example.grabapp.ui.user.ActivityUser
import kotlinx.coroutines.launch

class RegisterActivity : BaseActivity<ActivityRegisterBinding, RegisterViewModel>() {

    companion object {
        private const val CURRENT_STEP = "current_step"
    }

    private var currentStep: RegisterStep = RegisterStep.AVATAR
    private val adapter: RegisterPageAdapter by lazy { RegisterPageAdapter(this) }

    override fun getLazyBinding(): Lazy<ActivityRegisterBinding> =
        lazy { ActivityRegisterBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<RegisterViewModel> =
        lazy { ViewModelProvider(this)[RegisterViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar()
        setupViewPager(savedInstanceState)
        setupListeners()
        setupObserve()
        updateNextButtonState(false)
    }

    override fun handleInsets(v: View, insets: Insets) {
        binding.imageView1.setPadding(0, -insets.top, 0, 0)
        binding.toolbar.setPadding(0, insets.top / 2, 0, 0)
    }

    private fun setupListeners() {
        binding.apply {
            tvNext.onClickWithScale {
                handleNextClick()
            }

            tvPrevious.onClickWithScale {
                handlePreviousClick()
            }

            toolbar.setNavigationOnClickListener {
                handleToolbarNavigationClick()
            }
        }
    }

    private fun setupObserve() {
        lifecycleScope.launch {
            viewModel.isIdentificationCardValid.collect { isValid ->
                if (currentStep == RegisterStep.IDENTIFICATION_CARD) {
                    updateNextButtonState(isValid)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.isDrivingLicenseValid.collect { isValid ->
                if (currentStep == RegisterStep.DRIVING_LICENSE) {
                    updateNextButtonState(isValid)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.isEmergencyContactValid.collect { isValid ->
                if (currentStep == RegisterStep.EMERGENCY_CONTACT) {
                    updateNextButtonState(isValid)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.isBankAccountValid.collect { isValid ->
                if (currentStep == RegisterStep.BANK_ACCOUNT) {
                    updateNextButtonState(isValid)
                }
            }
        }
    }

    fun updateNextButtonState(isEnabled: Boolean) {
        binding.tvNext.alpha = if (isEnabled) 1f else 0.5f
        binding.tvNext.isEnabled = isEnabled
    }

    private fun updateUIForCurrentStep() {
        val isVehicleRegistration = currentStep == RegisterStep.VEHICLE_INSURANCE
        binding.indicator.visibility = if (isVehicleRegistration) View.GONE else View.VISIBLE
        binding.llAction.visibility = if (isVehicleRegistration) View.GONE else View.VISIBLE
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back)
            title = ""
        }
        binding.toolbar.setNavigationOnClickListener {
            handleToolbarNavigationClick()
        }
    }

    private fun handleToolbarNavigationClick() {
        if (currentStep == RegisterStep.VEHICLE_INSURANCE) {
            startActivity<ActivityUser>()
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CURRENT_STEP, currentStep.ordinal)
    }

    private fun setupViewPager(savedInstanceState: Bundle?) {
        binding.viewPager2.apply {
            adapter = this@RegisterActivity.adapter
            isUserInputEnabled = false
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    currentStep = RegisterStep.entries[position]
                    updateUIForCurrentStep()
                    when (currentStep) {
                        RegisterStep.IDENTIFICATION_CARD -> {
                            updateNextButtonState(viewModel.isIdentificationCardValid.value)
                        }

                        RegisterStep.DRIVING_LICENSE -> {
                            updateNextButtonState(viewModel.isDrivingLicenseValid.value)
                        }

                        RegisterStep.EMERGENCY_CONTACT -> {
                            updateNextButtonState(viewModel.isEmergencyContactValid.value)
                        }

                        RegisterStep.BANK_ACCOUNT -> {
                            updateNextButtonState(viewModel.isBankAccountValid.value)
                        }

                        else -> {
                            updateNextButtonState(true)
                        }
                    }
                }
            })
        }
        binding.indicator.attachTo(binding.viewPager2)

        if (savedInstanceState == null) {
            currentStep = RegisterStep.AVATAR
            binding.viewPager2.setCurrentItem(currentStep.ordinal, false)
        } else {
            val savedStepOrdinal =
                savedInstanceState.getInt(CURRENT_STEP, RegisterStep.AVATAR.ordinal)
            currentStep =
                RegisterStep.entries.getOrElse(savedStepOrdinal) { RegisterStep.AVATAR }
            binding.viewPager2.setCurrentItem(currentStep.ordinal, false)
        }
        updateUIForCurrentStep()
    }

    private fun handleNextClick() {
        when (currentStep) {
            RegisterStep.IDENTIFICATION_CARD -> {
                viewModel.updateIdentificationCardValidation()
                if (!viewModel.isIdentificationCardValid.value) {
                    showIdentificationCardValidationErrors()
                    return
                }
            }

            RegisterStep.DRIVING_LICENSE -> {
                viewModel.updateDrivingLicenseValidation()
                if (!viewModel.isDrivingLicenseValid.value) {
                    showDrivingLicenseValidationErrors()
                    return
                }
            }

            RegisterStep.EMERGENCY_CONTACT -> {
                viewModel.updateEmergencyContactValidation()
                if (!viewModel.isEmergencyContactValid.value) {
                    showEmergencyContactValidationErrors()
                    return
                }
            }

            RegisterStep.BANK_ACCOUNT -> {
                viewModel.updateBankAccountValidation()
                if (!viewModel.isBankAccountValid.value) {
                    showBankAccountValidationErrors()
                    return
                }
            }

            else -> {}
        }

        if (currentStep.isLastStep()) {
            startActivity<ProfileReviewActivity> {}
        } else {
            val nextStep = currentStep.getNextStep()
            nextStep?.let { navigateToStep(it) }
        }
    }

    private fun showIdentificationCardValidationErrors() {
        val missingFields = viewModel.identificationCard.getMissingFields()
        if (missingFields.isNotEmpty()) {
            val errorMessage = when {
                missingFields.contains("Ảnh mặt trước") -> "Vui lòng upload ảnh mặt trước"
                missingFields.contains("Ảnh mặt sau") -> "Vui lòng upload ảnh mặt sau"
                missingFields.contains("Số CMND/CCCD") -> "Vui lòng nhập số CMND/CCCD (8-12 chữ số)"
                missingFields.contains("Ngày sinh") -> "Vui lòng chọn ngày sinh"
                missingFields.contains("Ngày sinh phải trước ngày hiện tại và trên 18 tuổi") -> "Ngày sinh phải trước ngày hiện tại và trên 18 tuổi"
                missingFields.contains("Ngày cấp") -> "Vui lòng chọn ngày cấp"
                missingFields.contains("Ngày cấp phải trước ngày hiện tại") -> "Ngày cấp phải trước ngày hiện tại"
                missingFields.contains("Ngày cấp phải sau ngày sinh") -> "Ngày cấp phải sau ngày sinh"
                missingFields.contains("Nơi cấp") -> "Vui lòng chọn nơi cấp"
                missingFields.contains("Ngày hết hạn") -> "Vui lòng chọn ngày hết hạn"
                missingFields.contains("Ngày hết hạn phải sau ngày hiện tại") -> "Ngày hết hạn phải sau ngày hiện tại"
                missingFields.contains("Ngày hết hạn phải sau ngày cấp") -> "Ngày hết hạn phải sau ngày cấp"
                missingFields.contains("Tỉnh") -> "Vui lòng chọn tỉnh"
                missingFields.contains("Giới tính") -> "Vui lòng chọn giới tính"
                else -> "Vui lòng điền đầy đủ thông tin"
            }
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDrivingLicenseValidationErrors() {
        val missingFields = viewModel.drivingLicense.getMissingFields()
        if (missingFields.isNotEmpty()) {
            val errorMessage = when {
                missingFields.contains("Ảnh mặt trước") -> "Vui lòng upload ảnh mặt trước"
                missingFields.contains("Ảnh mặt sau") -> "Vui lòng upload ảnh mặt sau"
                missingFields.contains("Số bằng lái xe") -> "Vui lòng nhập số bằng lái xe (chỉ số hoặc chữ in hoa, tối đa 12 ký tự)"
                missingFields.contains("Hạng bằng lái") -> "Vui lòng chọn hạng bằng lái"
                missingFields.contains("Ngày cấp") -> "Vui lòng chọn ngày cấp"
                missingFields.contains("Ngày cấp phải trước ngày hiện tại") -> "Ngày cấp phải trước ngày hiện tại"
                else -> "Vui lòng điền đầy đủ thông tin"
            }
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showEmergencyContactValidationErrors() {
        val missingFields = viewModel.emergencyContact.getMissingFields()
        if (missingFields.isNotEmpty()) {
            val errorMessage = when {
                missingFields.contains("Tên người liên hệ") -> "Vui lòng nhập tên người liên hệ"
                missingFields.contains("Mối quan hệ") -> "Vui lòng chọn mối quan hệ"
                missingFields.contains("Số điện thoại") -> "Vui lòng nhập số điện thoại"
                missingFields.contains("Số điện thoại không được quá 10 ký tự") -> "Số điện thoại không được quá 10 ký tự"
                missingFields.contains("Địa chỉ") -> "Vui lòng nhập địa chỉ"
                missingFields.contains("Xã/Phường") -> "Vui lòng nhập xã/phường"
                missingFields.contains("Tỉnh/Thành phố") -> "Vui lòng chọn tỉnh/thành phố"
                else -> "Vui lòng điền đầy đủ thông tin"
            }
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showBankAccountValidationErrors() {
        val missingFields = viewModel.bankAccount.getMissingFields()
        if (missingFields.isNotEmpty()) {
            val errorMessage = when {
                missingFields.contains("Tên tài khoản") -> "Vui lòng nhập tên tài khoản"
                missingFields.contains("Tên tài khoản chỉ được chứa chữ cái") -> "Tên tài khoản chỉ được chứa chữ cái"
                missingFields.contains("Số tài khoản") -> "Vui lòng nhập số tài khoản"
                missingFields.contains("Số tài khoản phải là số và nhiều hơn 9 chữ số") -> "Số tài khoản phải là số và nhiều hơn 9 chữ số"
                missingFields.contains("Tên ngân hàng") -> "Vui lòng chọn tên ngân hàng"
                missingFields.contains("Xác nhận thông tin") -> "Vui lòng xác nhận thông tin tài khoản"
                else -> "Vui lòng điền đầy đủ thông tin"
            }
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handlePreviousClick() {
        if (!currentStep.isFirstStep()) {
            val previousStep = currentStep.getPreviousStep()
            previousStep?.let { navigateToStep(it) }
        }
    }

    private fun navigateToStep(step: RegisterStep) {
        binding.viewPager2.setCurrentItem(step.ordinal, true)
        currentStep = step
    }
}
