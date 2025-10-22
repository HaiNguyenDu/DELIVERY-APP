package com.example.grabapp.ui.user

import android.os.Bundle
import android.view.View
import androidx.core.graphics.Insets
import com.example.grabapp.R
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityUserBinding
import com.example.grabapp.driver.home.DriverHomeActivity
import com.example.grabapp.extention.onClickWithScale
import com.example.grabapp.extention.startActivity
import com.example.grabapp.ui.splash.NoViewModel
import com.example.grabapp.view.DialogEditUser
import com.example.grabapp.view.DialogEditUserListener

class ActivityUser : BaseActivity<ActivityUserBinding, NoViewModel>() {
    override fun getLazyBinding(): Lazy<ActivityUserBinding> = lazy {
        ActivityUserBinding.inflate(layoutInflater)
    }

    override fun getLazyViewModel(): Lazy<NoViewModel> = lazy {
        NoViewModel(application)
    }

    override fun handleInsets(v: View, insets: Insets) {
        super.handleInsets(v, insets)
        v.setPadding(insets.left, 0, insets.right, insets.bottom)
        binding.layoutToolbar.setPadding(0, insets.top, 0, binding.layoutToolbar.paddingBottom)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeView()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.anim_translate_in_right,
            R.anim.anim_translate_out_left
        )
    }

    private fun observeView() {
        binding.apply {
            tvSwitchToDriver.onClickWithScale {
                startActivity<DriverHomeActivity> {
                    /*no-op*/
                }
            }
            btnBack.setOnClickListener {
                finish()
            }
            btnUserDetail.setOnClickListener {
                DialogEditUser(this@ActivityUser).setListener(
                    object : DialogEditUserListener {
                        override fun onSave() {
                        }

                        override fun onLogOut() {
                        }

                    }
                ).show()
            }
        }
    }
}