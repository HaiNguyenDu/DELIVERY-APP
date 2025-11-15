package com.example.grabapp.driver.transportation

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityTransportationBinding
import com.example.grabapp.driver.confirm_driver.ConfirmDriverActivity
import com.example.grabapp.extention.onClickWithScale
import com.example.grabapp.extention.startActivity
import com.example.grabapp.view.bottom_sheet.TransportationBottomSheet
import kotlinx.coroutines.launch

class TransportationActivity :
    BaseActivity<ActivityTransportationBinding, TransportationViewModel>() {

    override fun getLazyBinding(): Lazy<ActivityTransportationBinding> =
        lazy { ActivityTransportationBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<TransportationViewModel> =
        lazy { TransportationViewModel(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        setupListeners()
        setupObserve()
    }

    private fun setupView() {
        val initialTransportation = viewModel.selectedTransportation.value
        binding.tvTransportationName.text = getString(initialTransportation.stringResId)
    }

    private fun setupListeners() {
        binding.apply {
            layoutService.onClickWithScale {
                showTransportationBottomSheet()
            }
            tvNext.onClickWithScale {
                startActivity<ConfirmDriverActivity> {
                    /*no-op*/
                }
            }
        }
    }

    private fun setupObserve() {
        lifecycleScope.launch {
            viewModel.selectedTransportation.collect { transportation ->
                binding.tvTransportationName.text = getString(transportation.stringResId)
            }
        }
    }

    private fun showTransportationBottomSheet() {
        val currentSelected = viewModel.selectedTransportation.value
        TransportationBottomSheet(
            selectedTransportation = currentSelected,
            onTransportationSelected = { transportation ->
                viewModel.setSelectedTransportation(transportation)
            }
        ).show(supportFragmentManager)
    }
}
