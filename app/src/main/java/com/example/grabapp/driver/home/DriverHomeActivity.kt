package com.example.grabapp.driver.home

import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityDriverHomeBinding

class DriverHomeActivity : BaseActivity<ActivityDriverHomeBinding, DriverHomeViewModel>() {
    override fun getLazyBinding(): Lazy<ActivityDriverHomeBinding> =
        lazy { ActivityDriverHomeBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverHomeViewModel> {
        return lazy { DriverHomeViewModel(application) }
    }

}
