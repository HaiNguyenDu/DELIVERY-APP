package com.example.grabapp.ui.address_selection

import android.Manifest
import android.content.pm.PackageItemInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.addCallback
import androidx.annotation.RequiresPermission
import androidx.core.graphics.Insets
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityAddressSelectionBinding
import com.example.grabapp.domain.model.order.PackageItemModel
import com.example.grabapp.ui.address_selection.adapter.AddressSelectionPageAdapter
import kotlinx.coroutines.launch

class AddressSelectionActivity :
    BaseActivity<ActivityAddressSelectionBinding, AddressSelectionViewModel>() {

    private lateinit var viewPageAdapter: AddressSelectionPageAdapter

    override fun getLazyBinding(): Lazy<ActivityAddressSelectionBinding> = lazy {
        ActivityAddressSelectionBinding.inflate(LayoutInflater.from(this))
    }

    override fun getLazyViewModel(): Lazy<AddressSelectionViewModel> = lazy {
        ViewModelProvider(this)[AddressSelectionViewModel::class.java]
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        observerView()
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun initView() {
        viewModel.addPackageInfo(PackageItemModel())
        viewPageAdapter = AddressSelectionPageAdapter(this)
        binding.viewPage2.adapter = viewPageAdapter
        binding.viewPage2.isUserInputEnabled = false
        viewModel.getCurrentLocation()
        onBackPressedDispatcher.addCallback {
            val index = binding.viewPage2.currentItem
            if(index==0) finish()
            else viewModel.setPage(index-1)
        }
    }

    override fun handleInsets(v: View, insets: Insets) {
    }

    private fun observerView() {
        lifecycleScope.launch {
            viewModel.pagePosition.collect {
                binding.viewPage2.setCurrentItem(it,true)
            }
        }
        lifecycleScope.launch {
            viewModel.isCreateSuccess.collect {
                if(it) finish()
            }
        }
    }
}