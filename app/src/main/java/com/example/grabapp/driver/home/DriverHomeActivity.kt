package com.example.grabapp.driver.home

import android.os.Bundle
import android.view.View
import androidx.core.graphics.Insets
import com.example.grabapp.R
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityDriverHomeBinding
import com.example.grabapp.driver.driver_income.DriverIncomeActivity
import com.example.grabapp.driver.driver_profile.DriverProfileActivity
import com.example.grabapp.extention.onClickWithScale
import com.example.grabapp.extention.startActivity

class DriverHomeActivity : BaseActivity<ActivityDriverHomeBinding, DriverHomeViewModel>() {
    override fun getLazyBinding(): Lazy<ActivityDriverHomeBinding> =
        lazy { ActivityDriverHomeBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverHomeViewModel> {
        return lazy { DriverHomeViewModel(application) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRootColor(getColor(R.color.bg_color))
        observeView()
    }

    override fun handleInsets(v: View, insets: Insets) {
        v.setPadding(insets.left, 0, insets.right, insets.bottom)
        binding.tvNotice.setPadding(
            0,
            insets.top,
            0,
            0
        )
    }

    private fun observeView() {
        binding.apply {
            llMoney.onClickWithScale {
                startActivity<DriverIncomeActivity> {
                    /*no-op*/
                }
            }
            ctlProfile.onClickWithScale {
                startActivity<DriverProfileActivity> {
                    /*no-op*/
                }
            }
        }
    }
}
