package com.example.grabapp.driver.login

import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityDriverLoginBinding

class DriverLoginActivity : BaseActivity<ActivityDriverLoginBinding, DriverLoginViewModel>() {
    override fun getLazyBinding(): Lazy<ActivityDriverLoginBinding> =
        lazy { ActivityDriverLoginBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverLoginViewModel> =
        lazy { DriverLoginViewModel(application) }
}
