package com.example.grabapp.driver.edit_profile

import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityEditProfileBinding

class EditProfileActivity : BaseActivity<ActivityEditProfileBinding, EditProfileViewModel>() {
    override fun getLazyBinding(): Lazy<ActivityEditProfileBinding> =
        lazy { ActivityEditProfileBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<EditProfileViewModel> =
        lazy { EditProfileViewModel(application) }

}
