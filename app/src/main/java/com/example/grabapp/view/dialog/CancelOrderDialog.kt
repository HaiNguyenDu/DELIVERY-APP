package com.example.grabapp.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabapp.base.BaseDialogFragment
import com.example.grabapp.databinding.DialogCancelOrderBinding
import com.example.grabapp.model.CancelOrderType
import com.example.grabapp.model.OrderStatus
import com.example.grabapp.view.dialog.adapter.CancelReasonAdapter

class CancelOrderDialog : BaseDialogFragment<DialogCancelOrderBinding>() {
    
    companion object {
        private const val ARG_ORDER_STATUS = "arg_order_status"
        
        fun newInstance(
            orderStatus: OrderStatus,
            onApplyClick: (CancelOrderType) -> Unit
        ): CancelOrderDialog {
            return CancelOrderDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_ORDER_STATUS, orderStatus.name)
                }
                this.onApplyClick = onApplyClick
            }
        }
    }
    
    private var adapter: CancelReasonAdapter? = null
    private var onApplyClick: ((CancelOrderType) -> Unit)? = null
    
    private val orderStatus: OrderStatus? by lazy {
        arguments?.getString(ARG_ORDER_STATUS)?.let { statusName ->
            try {
                OrderStatus.valueOf(statusName)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogCancelOrderBinding {
        return DialogCancelOrderBinding.inflate(inflater, container, false)
    }

    override fun setUpInit() {
        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        val cancelReasons = getCancelReasonsForStatus(orderStatus)
        
        if (cancelReasons.isNotEmpty()) {
            binding.rvCancelReason.visibility = android.view.View.VISIBLE
            binding.rvCancelReason.layoutManager = LinearLayoutManager(requireContext())
            adapter = CancelReasonAdapter(cancelReasons) { selectedItem ->
            }
            binding.rvCancelReason.adapter = adapter
        } else {
            binding.rvCancelReason.visibility = android.view.View.GONE
        }
    }

    private fun getCancelReasonsForStatus(status: OrderStatus?): List<CancelOrderType> {
        return when (status) {
            OrderStatus.ARRIVED_PICKUP,
            OrderStatus.DRIVER_EN_ROUTE_PICKUP,
            OrderStatus.DRIVER_ASSIGNED -> {
                listOf(
                    CancelOrderType.PICKUP_ATTEMPT_FAILED,
                    CancelOrderType.DRIVER_ISSUE_REPORTED
                )
            }
            OrderStatus.PACKAGE_PICKED,
            OrderStatus.EN_ROUTE_DELIVERY,
            OrderStatus.ARRIVED_DELIVERY -> {
                listOf(
                    CancelOrderType.DELIVERY_ATTEMPT_FAILED,
                    CancelOrderType.DRIVER_ISSUE_REPORTED
                )
            }
            else -> emptyList()
        }
    }

    private fun setupClickListeners() {
        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        binding.tvApply.setOnClickListener {
            val selectedItem = adapter?.getSelectedItem()
            if (selectedItem != null) {
                onApplyClick?.invoke(selectedItem)
                dismiss()
            }
        }
    }
}
