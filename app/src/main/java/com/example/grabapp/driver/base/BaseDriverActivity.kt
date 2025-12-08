package com.example.grabapp.driver.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.base.BaseViewModel
import com.example.grabapp.data.ConnectionStorage
import com.example.grabapp.data.repository.OrderRepository
import com.example.grabapp.driver.home.data.ConnectionState
import com.example.grabapp.driver.order_detail.OrderDetailActivity
import com.example.grabapp.extention.startActivity
import com.example.grabapp.model.Order
import com.example.grabapp.service.MyFirebaseMessagingService
import com.example.grabapp.util.OrderMapper
import com.example.grabapp.view.dialog.NewOrderedDialog
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.launch

abstract class BaseDriverActivity<T : ViewBinding, V : BaseViewModel> :
    BaseActivity<T, V>() {

    private val connectionStorage: ConnectionStorage by lazy {
        ConnectionStorage(this)
    }

    private val orderRepository: OrderRepository by lazy {
        OrderRepository(this)
    }

    private val newOrderReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == MyFirebaseMessagingService.ACTION_NEW_ORDER) {
                val orderId = intent.getStringExtra(MyFirebaseMessagingService.EXTRA_ORDER_ID)
                orderId?.let {
                    handleNewOrderNotification(it)
                } ?: run {
                }
            } else {
            }
        }
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        registerNewOrderReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(newOrderReceiver)
        } catch (e: Exception) {
        }
    }

    private fun registerNewOrderReceiver() {
        val filter = IntentFilter(MyFirebaseMessagingService.ACTION_NEW_ORDER)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(newOrderReceiver, filter, 2)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(newOrderReceiver, filter)
        }
    }

    private fun handleNewOrderNotification(orderId: String) {
        val connectionState = connectionStorage.getConnectionState()
        if (connectionState != ConnectionState.CONNECTED) {
            return
        }

        if (isDialogAlreadyShowing()) {
            return
        }

        fetchAndShowOrderDialog(orderId)
    }

    private fun isDialogAlreadyShowing(): Boolean {
        val existingDialog = supportFragmentManager.findFragmentByTag("NewOrderedDialog")
        return existingDialog != null && existingDialog.isAdded
    }

    private fun fetchAndShowOrderDialog(orderId: String) {
        lifecycleScope.launch {
            try {
                val result = orderRepository.getOrderById(orderId)
                when (result) {
                    is OrderRepository.OrderResult.Success -> {
                        val order = OrderMapper.mapToOrder(result.response)
                        val currentState = connectionStorage.getConnectionState()
                        if (currentState == ConnectionState.CONNECTED) {
                            showNewOrderDialog(order)
                        } else {
                        }
                    }

                    is OrderRepository.OrderResult.Error -> {
                        Log.e("BaseDriverActivity", "Không tìm thấy order với ID: $orderId")
                    }
                }
            } catch (e: Exception) {
                Log.e("BaseDriverActivity", "Lỗi khi fetch order: ${e.message}", e)
            }
        }
    }

    private fun showNewOrderDialog(order: Order) {
        NewOrderedDialog.newInstance(order).apply {
            onSkipOrder = {
                this@apply.dismiss()
            }
            onAcceptOrder = { acceptedOrder ->
                this@apply.dismiss()
                startActivity<OrderDetailActivity> {
                    putExtra("extra_order", acceptedOrder)
                }
            }
        }.show(supportFragmentManager, "NewOrderedDialog")
    }
}
