package com.example.grabapp.driver.driver_income.period_income

import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityPeriodIncomeBinding

class PeriodIncomeActivity : BaseActivity<ActivityPeriodIncomeBinding, PeriodIncomeViewModel>() {
    override fun getLazyBinding(): Lazy<ActivityPeriodIncomeBinding> =
        lazy { ActivityPeriodIncomeBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<PeriodIncomeViewModel> {
        return lazy { PeriodIncomeViewModel(application) }
    }
}
