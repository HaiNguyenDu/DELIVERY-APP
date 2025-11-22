package com.example.grabapp.ui.address_selection.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.graphics.Insets
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentDetailOrderBinding
import com.example.grabapp.domain.enum.EditTextEnum
import com.example.grabapp.ui.address_selection.AddressSelectionViewModel
import com.example.grabapp.ui.address_selection.adapter.AddressSelectionPageAdapter
import kotlinx.coroutines.launch

class DetailOrderFragment : BaseFragment<FragmentDetailOrderBinding, AddressSelectionViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentDetailOrderBinding> = lazy {
        FragmentDetailOrderBinding.inflate(LayoutInflater.from(context))
    }

    override fun getLazyViewModel(): Lazy<AddressSelectionViewModel> = lazy {
        ViewModelProvider(requireActivity())[AddressSelectionViewModel::class.java]
    }

    override fun setUpClick() {
        binding.tvPuAddress.setOnClickListener {
            viewModel.setLastEdtTextClicked(EditTextEnum.PICK_UP)
            DialogLocationInfoFragment().show(
                requireActivity().supportFragmentManager,
                "DetailPUInfo"
            )
        }
        binding.tvDrAddress.setOnClickListener {
            viewModel.setLastEdtTextClicked(EditTextEnum.DROP_OFF)
            DialogLocationInfoFragment().show(
                requireActivity().supportFragmentManager,
                "DetailDRInfo"
            )
        }
        binding.toolBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.btnNext.setOnClickListener {
            viewModel.setPage(AddressSelectionPageAdapter.FRAGMENT_CHECK_DIRECTION)
        }
        binding.tvDetailPackage.setOnClickListener {
            DetailPackageFragment().show(
                childFragmentManager, "Detail package"
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observerData()
    }

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        view.setPadding(inset.left, 0, inset.right, bottomInset)
        binding.toolBar.apply {
            setPadding(0, inset.top, 0, 0)
        }
    }

    private fun observerData() {
        lifecycleScope.launch {
            viewModel.pickUpAddress.collect {
                binding.tvPuAddress.text = it.getFormattedAddress()
            }
        }

        lifecycleScope.launch {
            viewModel.dropOffAddress.collect {
                binding.tvDrAddress.text = it.getFormattedAddress()
            }
        }
        lifecycleScope.launch {
            viewModel.packageInfo.collect { packageInfo ->
                packageInfo?.let {
                    val detailText = packageInfo.toString()
                    binding.tvDetailPackage.text = detailText
                }
            }
        }
    }
}