package com.example.grabapp.driver.driver_profile

import android.os.Bundle
import com.example.grabapp.R
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityDriverProfileBinding

class DriverProfileActivity : BaseActivity<ActivityDriverProfileBinding, DriverProfileViewModel>() {
    override fun getLazyBinding(): Lazy<ActivityDriverProfileBinding> =
        lazy { ActivityDriverProfileBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverProfileViewModel> {
        return lazy { DriverProfileViewModel(application) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setTitle(R.string.income)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_back)
        }
        binding.apply {
            toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}
