package com.example.grabapp.driver.confirm_driver.eligibility

import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityDriverEligibilityBinding

class DriverEligibilityActivity :
    BaseActivity<ActivityDriverEligibilityBinding, DriverEligibilityViewModel>() {
    override fun getLazyBinding(): Lazy<ActivityDriverEligibilityBinding> =
        lazy { ActivityDriverEligibilityBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverEligibilityViewModel> =
        lazy { DriverEligibilityViewModel(application) }

}
