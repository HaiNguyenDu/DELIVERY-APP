package com.example.grabapp.driver.register

import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityRegisterBinding

class RegisterActivity : BaseActivity<ActivityRegisterBinding, RegisterViewModel>() {
    override fun getLazyBinding(): Lazy<ActivityRegisterBinding> =
        lazy { ActivityRegisterBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<RegisterViewModel> =
        lazy { RegisterViewModel(application) }
}
