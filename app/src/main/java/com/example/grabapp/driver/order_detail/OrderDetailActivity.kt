package com.example.grabapp.driver.order_detail

import android.os.Bundle
import android.view.View
import androidx.core.graphics.Insets
import com.example.grabapp.R
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityDetailOrderBinding
import com.example.grabapp.extention.onClickWithScale
import com.example.grabapp.model.Order
import android.os.Parcelable
import com.example.grabapp.model.OrderState

class OrderDetailActivity : BaseActivity<ActivityDetailOrderBinding, OrderDetailViewModel>() {

    companion object {
        private const val EXTRA_ORDER = "extra_order"
    }

    private lateinit var order: Order
    private var currentState: OrderState = OrderState.RECEIVED_ORDER

    override fun getLazyBinding(): Lazy<ActivityDetailOrderBinding> =
        lazy { ActivityDetailOrderBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<OrderDetailViewModel> =
        lazy { OrderDetailViewModel(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupClickListeners()
        getOrderFromIntent()
        setupToolbar()
        setupOrderInformation()
        setupStateManagement()
    }

    private fun setupClickListeners() {
        binding.tvDeliveryComplete.onClickWithScale {
            moveToNextState()
        }
    }

    private fun getOrderFromIntent() {
        order = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_ORDER, Order::class.java) ?: Order.getMockOrder()
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Parcelable>(EXTRA_ORDER) as? Order ?: Order.getMockOrder()
        }
    }

    private fun setupToolbar() {
        binding.apply {
            ivBack.onClickWithScale {
                onBackPressedDispatcher.onBackPressed()
            }
            tvOrderId.text = order.orderId
        }
    }

    private fun setupOrderInformation() {
        binding.apply {
            tvPickUpName.text = order.pickerName
            tvPickUpAddress.text = order.pickerAddress
            tvCallInAdvance.visibility = if (order.notion != null) View.VISIBLE else View.GONE
            tvCallInAdvance.text = order.notion

            tvReceiverName.text = order.deliveryName
            tvReceiverAddress.text = order.deliveryAddress
            tvCallReceiver.visibility = if (order.isBusinessHours) View.VISIBLE else View.GONE

            tvGoodsDes.text = order.orderType.displayName
            tvGoodsWeight.text = order.goodsWeight
            tvDistance.text = order.distance
            tvDeliveryFee.text = "${String.format("%,d", order.income)}đ"
        }
    }

    private fun setupStateManagement() {
        currentState = OrderState.RECEIVED_ORDER
        updateUIForCurrentState()
    }

    private fun moveToNextState() {
        currentState = when (currentState) {
            OrderState.RECEIVED_ORDER -> OrderState.COMING_TO_PICKUP
            OrderState.COMING_TO_PICKUP -> OrderState.RECEIVED_GOODS
            OrderState.RECEIVED_GOODS -> OrderState.DELIVERING
            OrderState.DELIVERING -> OrderState.DELIVERED
            OrderState.DELIVERED -> return
            OrderState.CANCELED -> return
        }
        updateUIForCurrentState()
    }

    private fun updateUIForCurrentState() {
        val paddingPx = resources.getDimensionPixelSize(R.dimen.size_8)

        when (currentState) {
            OrderState.RECEIVED_ORDER -> {
                binding.apply {
                    ivReceivedOrder.setImageResource(R.drawable.ic_white_tick)
                    ivReceivedOrder.setBackgroundResource(R.drawable.bg_gradient_finding_orders_100)
                    ivReceivedOrderStatus.visibility = View.VISIBLE
                }
                binding.tvDeliveryComplete.text = getString(R.string.ang_n_l_y_h_ng)
            }

            OrderState.COMING_TO_PICKUP -> {
                binding.apply {
                    ivComingTo.setImageResource(R.drawable.ic_white_tick)
                    ivComingTo.setBackgroundResource(R.drawable.bg_gradient_finding_orders_100)
                    ivComingTo.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
                    tvComingTo.setTextColor(getColor(R.color.black))
                    ivComingToStatus.visibility = View.VISIBLE

                    tvDeliveryComplete.text = getString(R.string.received_goods)
                }
            }

            OrderState.RECEIVED_GOODS -> {
                binding.apply {
                    ivReceivedGood.setImageResource(R.drawable.ic_white_tick)
                    ivReceivedGood.setBackgroundResource(R.drawable.bg_gradient_finding_orders_100)
                    ivReceivedGood.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
                    tvReceivedGood.setTextColor(getColor(R.color.black))
                    ivReceivedGoodStatus.visibility = View.VISIBLE

                    tvDeliveryComplete.text = getString(R.string.ang_giao_h_ng)
                }
            }

            OrderState.DELIVERING -> {
                binding.apply {
                    ivDelivering.setImageResource(R.drawable.ic_white_tick)
                    ivDelivering.setBackgroundResource(R.drawable.bg_gradient_finding_orders_100)
                    ivDelivering.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
                    tvDelivering.setTextColor(getColor(R.color.black))
                    ivDeliveringStatus.visibility = View.VISIBLE

                    tvDeliveryComplete.text = getString(R.string.ho_n_th_nh_giao_h_ng)
                }
            }

            OrderState.DELIVERED -> {
                binding.apply {
                    ivDelivered.setImageResource(R.drawable.ic_white_tick)
                    ivDelivered.setBackgroundResource(R.drawable.bg_gradient_finding_orders_100)
                    ivDelivered.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
                    tvDelivered.setTextColor(getColor(R.color.black))
                    ivDeliveredStatus.visibility = View.VISIBLE

                    tvDeliveryComplete.text = getString(R.string.chi_ti_t_n_h_ng)
                }
            }
            
            OrderState.CANCELED -> {
            }
        }
    }

    override fun handleInsets(v: View, insets: Insets) {
        binding.toolbar.setPadding(0, insets.top, 0, 0)
    }
}
