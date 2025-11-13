package com.example.grabapp.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.R
import com.example.grabapp.base.BaseDialogFragment
import com.example.grabapp.databinding.DialogNewOrderedBinding
import com.example.grabapp.extention.onClickWithScale
import com.example.grabapp.model.Order
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NewOrderedDialog : BaseDialogFragment<DialogNewOrderedBinding>() {

    var onSkipOrder: (() -> Unit)? = null
    var onAcceptOrder: (() -> Unit)? = null

    private var countdownJob: Job? = null
    private var currentCount = 15
    private val order: Order = Order.getMockOrder()

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
            tvCOD.text = if (order.hasCOD) getString(R.string.cod) else getString(R.string.kh_ng)

            tvIsNotion.visibility = if (order.notion != null) View.VISIBLE else View.GONE
            tvFragileGoods.visibility = if (order.fragileGoods) View.VISIBLE else View.GONE
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            tvSkipOrder.onClickWithScale {
                onSkipOrder?.invoke()
            }

            tvAcceptOrder.onClickWithScale {
                onAcceptOrder?.invoke()
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
