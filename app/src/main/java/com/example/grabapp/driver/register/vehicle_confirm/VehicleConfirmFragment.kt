package com.example.grabapp.driver.register.vehicle_confirm

import android.view.View
import androidx.core.graphics.Insets
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentVehicleConfirmBinding
import com.example.grabapp.driver.register.RegisterViewModel

class VehicleConfirmFragment :  BaseFragment<FragmentVehicleConfirmBinding, RegisterViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentVehicleConfirmBinding> =
        lazy { FragmentVehicleConfirmBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<RegisterViewModel> =
        lazy { RegisterViewModel(requireActivity().application) }

    override fun setUpClick() = Unit

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        view.setPadding(0, 0, 0, 0)
    }
}
