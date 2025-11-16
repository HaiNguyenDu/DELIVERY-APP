package com.example.grabapp.driver.register.vehicle_registration

import android.view.View
import androidx.core.graphics.Insets
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentVehicleRegistrationBinding
import com.example.grabapp.driver.register.RegisterViewModel

class VehicleRegistrationFragment : BaseFragment<FragmentVehicleRegistrationBinding, RegisterViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentVehicleRegistrationBinding> =
        lazy { FragmentVehicleRegistrationBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<RegisterViewModel> =
        lazy { RegisterViewModel(requireActivity().application) }

    override fun setUpClick() = Unit

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        view.setPadding(0, 0, 0, 0)
    }
}
