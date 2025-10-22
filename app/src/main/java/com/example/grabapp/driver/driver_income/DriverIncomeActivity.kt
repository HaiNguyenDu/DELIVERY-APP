package com.example.grabapp.driver.driver_income

import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityDriverIncomeBinding

class DriverIncomeActivity : BaseActivity<ActivityDriverIncomeBinding, DriverIncomeViewModel>() {
    override fun getLazyBinding(): Lazy<ActivityDriverIncomeBinding> =
        lazy { ActivityDriverIncomeBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverIncomeViewModel> {
        return lazy { DriverIncomeViewModel(application) }
    }
}
