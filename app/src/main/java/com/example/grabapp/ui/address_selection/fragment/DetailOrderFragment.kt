package com.example.grabapp.ui.address_selection.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.graphics.Insets
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabapp.R
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentDetailOrderBinding
import com.example.grabapp.domain.enum.EditTextEnum
import com.example.grabapp.domain.enum.PaymentTypeEnum
import com.example.grabapp.domain.model.order.PackageItemModel
import com.example.grabapp.extention.toMoneyFormat
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
            viewModel.setLastFocusEdt(EditTextEnum.PICK_UP)
            viewModel.setLastAddress(viewModel.orderForm.value.pickupAddress)
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
            if (viewModel.isHashInfoPackage()) {
                viewModel.setPage(AddressSelectionPageAdapter.FRAGMENT_CHECK_DIRECTION)
                viewModel.getDirection()
            } else
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
        viewModel.loadUser()
        val paymentMethods = PaymentTypeEnum.entries.map {
            it.label
        }
        val adapterPayment = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            paymentMethods
        )

        binding.edtPaymentMethod.setAdapter(adapterPayment)

        binding.edtPaymentMethod.setOnItemClickListener { parent, view, position, id ->
            val selectedMethod = parent.getItemAtPosition(position) as String
            when (selectedMethod) {

                PaymentTypeEnum.CASH.label -> {
                    viewModel.setPaymentType(PaymentTypeEnum.CASH)
                }

                PaymentTypeEnum.ONLINE.label -> {
                    viewModel.setPaymentType(PaymentTypeEnum.ONLINE)
                }
            }
        }
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
                viewModel.setLastFocusEdt(EditTextEnum.DROP_OFF)
                viewModel.setLastAddress(viewModel.getCurrentPackageInfo().dropOffAddress)
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

    private fun observerData() {
        lifecycleScope.launch {
            viewModel.orderForm.collect {
                binding.tvPuAddress.text = it.pickupAddress.detail
                binding.tvPuPhone.text = it.pickupAddress.phone
                binding.tvPuUsername.text = it.pickupAddress.name
                detailOrderItemAdapter.setListOrder(
                    it.listPackageInfo,
                    viewModel.selectPackagePosition
                )
                if (viewModel.canCalculatePrice())
                    binding.tvCost.text =
                        viewModel.getPriceAndRoute().toDouble().toMoneyFormat() + "₫"

                if (viewModel.isHashInfoPackage()) {
                    binding.btnNext.alpha = 1f
                } else {
                    binding.btnNext.alpha = 0.6f
                }
            }
        }
    }
}