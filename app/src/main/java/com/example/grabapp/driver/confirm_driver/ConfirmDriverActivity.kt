package com.example.grabapp.driver.confirm_driver

import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityConfirmDriverBinding

class ConfirmDriverActivity : BaseActivity<ActivityConfirmDriverBinding, ConfirmDriverViewModel>() {
    override fun getLazyBinding(): Lazy<ActivityConfirmDriverBinding> =
        lazy { ActivityConfirmDriverBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<ConfirmDriverViewModel> =
        lazy { ConfirmDriverViewModel(application) }
}
