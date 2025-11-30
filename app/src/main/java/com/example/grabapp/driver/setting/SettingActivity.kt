package com.example.grabapp.driver.setting

import android.os.Bundle
import android.os.PersistableBundle
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivitySettingBinding
import com.example.grabapp.driver.edit_profile.EditProfileActivity
import com.example.grabapp.extention.onClickWithScale
import com.example.grabapp.extention.startActivity
import java.io.File

class SettingActivity : BaseActivity<ActivitySettingBinding, SettingViewModel>() {
    override fun getLazyBinding(): Lazy<ActivitySettingBinding> =
        lazy { ActivitySettingBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<SettingViewModel> = lazy { SettingViewModel(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initListener()
    }

    private fun initListener() {
        binding.apply {
            ctlEditProfile.onClickWithScale {
                startActivity<EditProfileActivity>()
            }
        }
    }
}
