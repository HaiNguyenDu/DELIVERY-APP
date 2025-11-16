package com.example.grabapp.driver.register

import android.os.Bundle
import androidx.activity.addCallback
import androidx.viewpager2.widget.ViewPager2
import com.example.grabapp.R
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityRegisterBinding
import com.example.grabapp.driver.profile_preview.ProfileReviewActivity
import com.example.grabapp.driver.register.adapter.RegisterPageAdapter
import com.example.grabapp.driver.register.data.RegisterStep
import com.example.grabapp.extention.onClickWithScale
import com.example.grabapp.extention.startActivity

class RegisterActivity : BaseActivity<ActivityRegisterBinding, RegisterViewModel>() {

    companion object {
        private const val CURRENT_STEP = "current_step"
    }

    private var currentStep: RegisterStep = RegisterStep.AVATAR
    private val adapter: RegisterPageAdapter by lazy { RegisterPageAdapter(this) }

    override fun getLazyBinding(): Lazy<ActivityRegisterBinding> =
        lazy { ActivityRegisterBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<RegisterViewModel> =
        lazy { RegisterViewModel(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar()
        setupViewPager(savedInstanceState)
        setupListeners()
        setupObserve()
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

    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back)
            title = ""
        }
        onBackPressedDispatcher.addCallback(this) {
            if (currentStep.isFirstStep()) {
                finish()
            } else {
                handlePreviousClick()
            }
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
        if (currentStep.isLastStep()) {
            startActivity<ProfileReviewActivity> {}
        } else {
            val nextStep = currentStep.getNextStep()
            nextStep?.let { navigateToStep(it) }
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
