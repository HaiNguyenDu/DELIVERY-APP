package com.example.grabapp.ui.address_selection.fragment

import android.view.LayoutInflater
import android.view.View
import androidx.core.graphics.Insets
import androidx.lifecycle.ViewModelProvider
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentMainOrderBinding
import com.example.grabapp.ui.address_selection.AddressSelectionViewModel
import com.example.grabapp.view.DialogAddressSelection

class MainOrderFragment : BaseFragment<FragmentMainOrderBinding, AddressSelectionViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentMainOrderBinding> = lazy {
        FragmentMainOrderBinding.inflate(LayoutInflater.from(context))
    }

    override fun getLazyViewModel(): Lazy<AddressSelectionViewModel> = lazy {
        ViewModelProvider(requireActivity())[AddressSelectionViewModel::class.java]
    }

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        view.setPadding(inset.left, 0, inset.right, bottomInset)
        binding.layoutHeader.apply {
            setPadding(paddingLeft, inset.top, paddingLeft, paddingBottom)
        }
    }

    override fun setUpClick() {
        binding.tvDropOffLocation.setOnClickListener {
            DialogAddressSelection(requireActivity()).show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DetailOrderFragment()
    }
}