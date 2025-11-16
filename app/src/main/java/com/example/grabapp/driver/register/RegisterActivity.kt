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
                onBackPressedDispatcher.onBackPressed()
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
    }

    fun updateNextButtonState(isEnabled: Boolean) {
        binding.tvNext.alpha = if (isEnabled) 1f else 0.5f
        binding.tvNext.isEnabled = isEnabled
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back)
            title = ""
        }
        binding.toolbar.setNavigationOnClickListener {
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
                    if (currentStep == RegisterStep.IDENTIFICATION_CARD) {
                        updateNextButtonState(viewModel.isIdentificationCardValid.value)
                    } else {
                        updateNextButtonState(true)
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
            currentStep = RegisterStep.entries.getOrElse(savedStepOrdinal) { RegisterStep.AVATAR }
            binding.viewPager2.setCurrentItem(currentStep.ordinal, false)
        }
    }

    private fun handleNextClick() {
        if (currentStep == RegisterStep.IDENTIFICATION_CARD) {
            viewModel.updateIdentificationCardValidation()
            if (!viewModel.isIdentificationCardValid.value) {
                showValidationErrors()
                return
            }
        }

        if (currentStep.isLastStep()) {
            startActivity<ProfileReviewActivity> {}
        } else {
            val nextStep = currentStep.getNextStep()
            nextStep?.let { navigateToStep(it) }
        }
    }

    private fun showValidationErrors() {
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
