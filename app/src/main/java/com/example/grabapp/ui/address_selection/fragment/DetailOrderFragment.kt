package com.example.grabapp.ui.address_selection.fragment

import android.view.LayoutInflater
import android.view.View
import androidx.core.graphics.Insets
import androidx.lifecycle.ViewModelProvider
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentDetailOrderBinding
import com.example.grabapp.ui.address_selection.AddressSelectionViewModel

class DetailOrderFragment: BaseFragment<FragmentDetailOrderBinding, AddressSelectionViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentDetailOrderBinding> = lazy {
        FragmentDetailOrderBinding.inflate(LayoutInflater.from(context))
    }

    override fun getLazyViewModel(): Lazy<AddressSelectionViewModel> = lazy {
        ViewModelProvider(requireActivity())[AddressSelectionViewModel::class.java]
    }

    override fun setUpClick() {

    }

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        view.setPadding(inset.left, 0, inset.right, bottomInset)
        binding.toolBar.apply {
            setPadding(0, inset.top, 0, 0)
        }
    }
}