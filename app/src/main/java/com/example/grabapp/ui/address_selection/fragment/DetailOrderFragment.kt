package com.example.grabapp.ui.address_selection.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.graphics.Insets
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabapp.R
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentDetailOrderBinding
import com.example.grabapp.domain.enum.EditTextEnum
import com.example.grabapp.domain.model.order.PackageItemModel
import com.example.grabapp.ui.address_selection.AddressSelectionViewModel
import com.example.grabapp.ui.address_selection.adapter.AddressSelectionPageAdapter
import com.example.grabapp.ui.address_selection.adapter.DetailOrderItemAdapter
import com.example.grabapp.view.SnackBarCustom
import kotlinx.coroutines.launch

class DetailOrderFragment : BaseFragment<FragmentDetailOrderBinding, AddressSelectionViewModel>() {
    private lateinit var detailOrderItemAdapter: DetailOrderItemAdapter

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
        binding.toolBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
            viewModel.clearOrderForm()
        }

        binding.btnNext.setOnClickListener {
            if (viewModel.isHashInfoPackage())
                viewModel.setPage(AddressSelectionPageAdapter.FRAGMENT_CHECK_DIRECTION)
            else
                SnackBarCustom(
                    view = binding.root,
                    message = getString(R.string.fill_full_info),
                    backgroundColor = context?.getColor(R.color.white)!!,
                    textColor = context?.getColor(R.color.green)!!,
                    bottomMarginDp = 100f,
                ).show()
        }

        binding.tvAddPackage.setOnClickListener {
            viewModel.addPackageInfo(PackageItemModel())
        }
    }

    fun initView() {
        detailOrderItemAdapter = DetailOrderItemAdapter(viewModel.orderForm.value.listPackageInfo)
        binding.rcvOrder.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = detailOrderItemAdapter
            isNestedScrollingEnabled = false
        }
        detailOrderItemAdapter.setListener(object :
            DetailOrderItemAdapter.DetailOrderItemAdapterListener {
            override fun onAddressClick(position: Int) {
                viewModel.selectPackagePosition = position
                viewModel.setLastEdtTextClicked(EditTextEnum.DROP_OFF)
                DialogLocationInfoFragment().show(
                    requireActivity().supportFragmentManager,
                    "DetailDRInfo"
                )
            }

            override fun onDeleteClick(position: Int) {

            }

            override fun onDetailPackageClick(position: Int) {
                viewModel.selectPackagePosition = position
                DetailPackageFragment().show(
                    childFragmentManager, "Detail package"
                )
            }

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observerData()
    }

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        view.setPadding(inset.left, 0, inset.right, bottomInset)
        binding.toolBar.apply {
            setPadding(0, inset.top, 0, 0)
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isHashInfoPackage()) {
            binding.btnNext.alpha = 1f
        } else {
            binding.btnNext.alpha = 0.6f
        }
    }

    private fun observerData() {
        lifecycleScope.launch {
            viewModel.orderForm.collect {
                binding.tvPuAddress.text = it.pickupAddress.detail
                detailOrderItemAdapter.setListOrder(
                    it.listPackageInfo,
                    viewModel.selectPackagePosition
                )
            }
        }
    }
}