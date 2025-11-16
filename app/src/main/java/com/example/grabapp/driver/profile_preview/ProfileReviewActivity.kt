package com.example.grabapp.driver.profile_preview

import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityProfileReviewBinding

class ProfileReviewActivity :
    BaseActivity<ActivityProfileReviewBinding, ProfileReviewViewModel>() {

    override fun getLazyBinding(): Lazy<ActivityProfileReviewBinding> =
        lazy { ActivityProfileReviewBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<ProfileReviewViewModel> =
        lazy { ProfileReviewViewModel(application) }

}
