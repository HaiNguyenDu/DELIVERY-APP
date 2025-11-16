package com.example.grabapp.driver.confirm_driver.eligibility

import android.os.Bundle
import com.example.grabapp.R
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityDriverEligibilityBinding
import com.example.grabapp.extention.onClickWithScale

class DriverEligibilityActivity :
    BaseActivity<ActivityDriverEligibilityBinding, DriverEligibilityViewModel>() {
    override fun getLazyBinding(): Lazy<ActivityDriverEligibilityBinding> =
        lazy { ActivityDriverEligibilityBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverEligibilityViewModel> =
        lazy { DriverEligibilityViewModel(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupListener()
        setupToolBar()
    }

    private fun setupToolBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back)
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.tvLogOut.setOnClickListener {
        }
    }

    private fun setupListener() {
        binding.ctlContinue.onClickWithScale {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
