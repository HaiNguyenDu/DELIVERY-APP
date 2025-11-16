package com.example.grabapp.driver.home.profile

import android.view.View
import androidx.core.graphics.Insets
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentDriverProfileBinding
import com.example.grabapp.driver.home.DriverHomeViewModel

class DriverProfileFragment : BaseFragment<FragmentDriverProfileBinding, DriverHomeViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentDriverProfileBinding> =
        lazy { FragmentDriverProfileBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverHomeViewModel> =
        lazy { DriverHomeViewModel(requireActivity().application) }

    override fun setUpClick() = Unit

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        binding.emptyView.setPadding(0, inset.top, 0, 0)
        binding.textView1.setPadding(0, inset.top / 2, 0, 0)
    }
}
