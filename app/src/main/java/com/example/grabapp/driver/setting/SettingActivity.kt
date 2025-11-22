package com.example.grabapp.driver.setting

import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivitySettingBinding

class SettingActivity : BaseActivity<ActivitySettingBinding, SettingViewModel>() {
    override fun getLazyBinding(): Lazy<ActivitySettingBinding> =
        lazy { ActivitySettingBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<SettingViewModel> = lazy { SettingViewModel(application) }
}
