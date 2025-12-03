package com.example.grabapp.driver.setting

import android.os.Bundle
import android.view.View
import androidx.core.graphics.Insets
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivitySettingBinding
import com.example.grabapp.driver.edit_profile.EditProfileActivity
import com.example.grabapp.extention.startActivity

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
            ctlEditProfile.setOnClickListener {
                startActivity<EditProfileActivity>()
            }
            flBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    override fun handleInsets(v: View, insets: Insets) {
        binding.root.setPadding(0, -insets.top, 0, 0)
    }
}
