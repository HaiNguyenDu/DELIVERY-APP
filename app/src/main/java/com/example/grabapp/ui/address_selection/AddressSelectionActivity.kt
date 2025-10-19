package com.example.grabapp.ui.address_selection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.graphics.Insets
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityAddressSelectionBinding
import com.example.grabapp.ui.address_selection.adapter.AddressSelectionPageAdapter

class AddressSelectionActivity :
    BaseActivity<ActivityAddressSelectionBinding, AddressSelectionViewModel>() {

    private lateinit var viewPageAdapter: AddressSelectionPageAdapter

    override fun getLazyBinding(): Lazy<ActivityAddressSelectionBinding> = lazy {
        ActivityAddressSelectionBinding.inflate(LayoutInflater.from(this))
    }

    override fun getLazyViewModel(): Lazy<AddressSelectionViewModel> = lazy {
        AddressSelectionViewModel(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        observerView()
    }

    private fun initView() {
        viewPageAdapter = AddressSelectionPageAdapter(this)
        binding.viewPage2.adapter = viewPageAdapter
    }

    override fun handleInsets(v: View, insets: Insets) {
    }
    private fun observerView() {

    }
}