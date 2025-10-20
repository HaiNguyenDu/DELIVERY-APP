package com.example.grabapp.ui.address_selection.fragment

import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProvider
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentDropOffInfoBinding
import com.example.grabapp.ui.address_selection.AddressSelectionViewModel

class DropOffInfoFragment : BaseFragment<FragmentDropOffInfoBinding, AddressSelectionViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentDropOffInfoBinding> = lazy {
        FragmentDropOffInfoBinding.inflate(LayoutInflater.from(context))
    }

    override fun getLazyViewModel(): Lazy<AddressSelectionViewModel> = lazy {
        ViewModelProvider(requireActivity())[AddressSelectionViewModel::class.java]
    }

    override fun setUpClick() {
    }
}