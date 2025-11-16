package com.example.grabapp.driver.register.vehicle_insurance

import android.view.View
import androidx.core.graphics.Insets
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentVehicleInsuranceBinding
import com.example.grabapp.driver.register.RegisterViewModel

class VehicleInsuranceFragment :
    BaseFragment<FragmentVehicleInsuranceBinding, RegisterViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentVehicleInsuranceBinding> =
        lazy { FragmentVehicleInsuranceBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<RegisterViewModel> =
        lazy { RegisterViewModel(requireActivity().application) }

    override fun setUpClick() = Unit

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        view.setPadding(0, 0, 0, 0)
    }
}
