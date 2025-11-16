package com.example.grabapp.driver.register.driving_license

import android.view.View
import androidx.core.graphics.Insets
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentDrivingLicenseBinding
import com.example.grabapp.driver.register.RegisterViewModel

class DrivingLicenseFragment : BaseFragment<FragmentDrivingLicenseBinding, RegisterViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentDrivingLicenseBinding> =
        lazy { FragmentDrivingLicenseBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<RegisterViewModel> =
        lazy { RegisterViewModel(requireActivity().application) }

    override fun setUpClick() = Unit

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        view.setPadding(0, 0, 0, 0)
    }
}
