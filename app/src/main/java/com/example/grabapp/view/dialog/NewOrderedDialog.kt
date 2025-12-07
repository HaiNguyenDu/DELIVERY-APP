package com.example.grabapp.view.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.R
import com.example.grabapp.base.BaseDialogFragment
import com.example.grabapp.data.repository.OrderRepository
import com.example.grabapp.databinding.DialogNewOrderedBinding
import com.example.grabapp.extention.onClickWithScale
import com.example.grabapp.model.Order
import com.example.grabapp.util.OrderMapper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NewOrderedDialog : BaseDialogFragment<DialogNewOrderedBinding>() {

    companion object {
        private const val ARG_ORDER = "arg_order"

        fun newInstance(order: Order): NewOrderedDialog {
            return NewOrderedDialog().apply {
                arguments = android.os.Bundle().apply {
                    putParcelable(ARG_ORDER, order)
                }
            }
        }
    }

    var onSkipOrder: (() -> Unit)? = null
    var onAcceptOrder: ((Order) -> Unit)? = null
    private var countdownJob: Job? = null
    private var currentCount = 15
    private val order: Order by lazy {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_ORDER, Order::class.java) ?: Order.Companion.getMockOrder()
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable<Order>(ARG_ORDER) ?: Order.Companion.getMockOrder()
        }
    }
    private val orderRepository: OrderRepository by lazy {
        OrderRepository(requireContext())
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogNewOrderedBinding {
        return DialogNewOrderedBinding.inflate(inflater, container, false)
    }

    override fun setUpInit() {
        setupOrderInform()
        setupClickListeners()
        startCountdown()
    }

    private fun setupOrderInform() {
        binding.apply {
            tvOrderId.text = order.orderId
            tvPickerName.text = order.pickerName
            tvPickerAddress.text = order.pickerAddress
            tvDeliveryName.text = order.deliveryName
            tvDeliveryAddress.text = order.deliveryAddress
            tvDistance.text = order.distance
            tvTime.text = order.estimatedTime
            tvIncome.text = "${String.format("%,d", order.income)}đ"
            // Hiển thị "Có" nếu có COD (codFee > 0), ngược lại "Không"
            tvCOD.text = if (order.hasCOD) "Có" else getString(R.string.kh_ng)

            // Hiển thị tvIsNotion nếu có note ở pickupAddress hoặc dropoffAddress
            tvIsNotion.visibility =
                if (order.notion != null || order.dropoffNote != null) View.VISIBLE else View.GONE
            tvFragileGoods.visibility = if (order.fragileGoods) View.VISIBLE else View.GONE
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            tvSkipOrder.onClickWithScale {
                onSkipOrder?.invoke()
            }

            tvAcceptOrder.onClickWithScale {
                handleAcceptOrder()
            }
        }
    }
    
    private fun handleAcceptOrder() {
        lifecycleScope.launch {
            try {
                val result = orderRepository.assignShipper(order.orderId)
                when (result) {
                    is OrderRepository.OrderResult.Success -> {
                        // Map OrderResponse sang Order với đầy đủ thông tin
                        val updatedOrder = OrderMapper.mapToOrder(result.response)
                        onAcceptOrder?.invoke(updatedOrder)
                    }
                    is OrderRepository.OrderResult.Error -> {
                        // Nếu lỗi, vẫn gọi callback với order hiện tại
                        onAcceptOrder?.invoke(order)
                    }
                }
            } catch (e: Exception) {
                // Nếu có exception, vẫn gọi callback với order hiện tại
                onAcceptOrder?.invoke(order)
            }
        }
    }

    private fun startCountdown() {
        currentCount = 15
        binding.tvCount.text = getString(R.string.second_count, currentCount)

        countdownJob = lifecycleScope.launch {
            while (currentCount > 0) {
                delay(1000)
                currentCount--
                if (currentCount > 0) {
                    binding.tvCount.text = getString(R.string.second_count, currentCount)
                } else {
                    onSkipOrder?.invoke()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countdownJob?.cancel()
        countdownJob = null
    }
}