package com.example.grabapp.driver.home

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.graphics.Insets
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.R
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.data.repository.AIServiceRepository
import com.example.grabapp.data.repository.FileRepository
import com.example.grabapp.data.repository.OrderRepository
import com.example.grabapp.databinding.FragmentHomeBinding
import com.example.grabapp.driver.home.data.ConnectionState
import com.example.grabapp.driver.order_detail.OrderDetailActivity
import com.example.grabapp.extention.onClickWithScale
import com.example.grabapp.extention.startActivity
import com.example.grabapp.model.Order
import com.example.grabapp.view.dialog.NewOrderedDialog
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment<FragmentHomeBinding, DriverHomeViewModel>() {

    private var connectionState: ConnectionState = ConnectionState.DISCONNECTED
    private var countdownJob: Job? = null

    override fun getLazyBinding(): Lazy<FragmentHomeBinding> =
        lazy { FragmentHomeBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverHomeViewModel> =
        lazy {
            val fileRepository = FileRepository()
            val aiServiceRepository = AIServiceRepository()
            val orderRepository = OrderRepository(requireContext())
            val factory = DriverHomeViewModelFactory(
                requireActivity().application,
                fileRepository,
                aiServiceRepository,
                orderRepository
            )
            ViewModelProvider(requireActivity(), factory)[DriverHomeViewModel::class.java]
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        connectionState = viewModel.getSavedConnectionState()
        updateUIState(connectionState)
        observeViewModel()
        viewModel.fetchOrders()
        viewModel.fetchDriverInfo()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.orders.collect { orders ->
                updateStatistics()
            }
        }

        lifecycleScope.launch {
            viewModel.driverInfo.collect { driverInfo ->
                driverInfo?.let {
                    binding.tvRate.text = String.format("%.1f", it.ratingAvg)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.updateStatusState.collect { state ->
                when (state) {
                    is UpdateStatusState.Success -> {
                    }
                    is UpdateStatusState.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "Lỗi cập nhật trạng thái: ${state.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun updateStatistics() {
        val todayOrdersCount = viewModel.getTodayOrdersCount()
        val totalCompletedIncome = viewModel.getTotalCompletedIncome()

        binding.apply {
            tvOrderedCount.text = todayOrdersCount.toString()
            tvIncome.text =
                com.example.grabapp.util.CurrencyFormatter.formatIncome(totalCompletedIncome)
        }
    }

    override fun setUpClick() {
        binding.ivToggleConnection.onClickWithScale {
            val newState = connectionState.toggle()
            connectionState = newState
            updateUIState(connectionState)
            handleConnectionStateChange(connectionState)
            val isAvailable = newState == ConnectionState.CONNECTED
            viewModel.updateDriverStatus(isAvailable)
        }
    }

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        binding.emptyView.setPadding(0, inset.top, 0, 0)
        binding.llTitle.setPadding(0, inset.top / 2, 0, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countdownJob?.cancel()
        countdownJob = null
    }

    private fun handleConnectionStateChange(state: ConnectionState) {
        countdownJob?.cancel()
        countdownJob = null

        if (state == ConnectionState.CONNECTED) {
            //startCountdownToShowDialog()
        }
    }

    private fun startCountdownToShowDialog() {
        countdownJob = lifecycleScope.launch {
            delay(2000)
            if (connectionState == ConnectionState.CONNECTED) {
                showNewOrderDialog()
            }
        }
    }

    private fun showNewOrderDialog() {
        val order = Order.getMockOrder()
        showNewOrderDialogWithOrder(order)
    }
    
    fun showNewOrderDialogByOrderId(orderId: String) {
        Log.d("HomeFragment", "=== showNewOrderDialogByOrderId ===")
        Log.d("HomeFragment", "OrderID: $orderId")
        Log.d("HomeFragment", "ConnectionState: $connectionState")
        
        // Chỉ hiển thị dialog nếu đang online
        if (connectionState != ConnectionState.CONNECTED) {
            Log.w("HomeFragment", "Driver không online, không hiển thị dialog. State: $connectionState")
            return
        }
        
        Log.d("HomeFragment", "Driver đang online, tìm order...")
        
        // Tìm order trong danh sách hiện tại trước
        val existingOrder = viewModel.findOrderById(orderId)
        if (existingOrder != null) {
            Log.d("HomeFragment", "Tìm thấy order trong danh sách hiện tại: ${existingOrder.orderId}")
            showNewOrderDialogWithOrder(existingOrder)
            return
        }
        
        Log.d("HomeFragment", "Không tìm thấy order trong danh sách, fetch order by id...")
        // Gọi API get order by id
        viewModel.fetchOrderById(orderId) { order ->
            Log.d("HomeFragment", "Kết quả fetch order: ${if (order != null) "Tìm thấy" else "Không tìm thấy"}")
            if (order != null) {
                Log.d("HomeFragment", "Order tìm thấy: ${order.orderId}, ConnectionState: $connectionState")
                if (connectionState == ConnectionState.CONNECTED) {
                    showNewOrderDialogWithOrder(order)
                } else {
                    Log.w("HomeFragment", "ConnectionState đã thay đổi, không hiển thị dialog")
                }
            } else {
                Log.e("HomeFragment", "Không tìm thấy order với ID: $orderId")
            }
        }
    }
    
    private fun showNewOrderDialogWithOrder(order: Order) {
        Log.d("HomeFragment", "=== showNewOrderDialogWithOrder ===")
        Log.d("HomeFragment", "OrderID: ${order.orderId}")
        
        // Kiểm tra xem dialog đã được hiển thị chưa
        val existingDialog = parentFragmentManager.findFragmentByTag("NewOrderedDialog")
        if (existingDialog != null && existingDialog.isAdded) {
            Log.w("HomeFragment", "Dialog đã được hiển thị, không hiển thị lại")
            return // Dialog đã được hiển thị, không hiển thị lại
        }
        
        Log.d("HomeFragment", "Hiển thị NewOrderedDialog với order: ${order.orderId}")
        NewOrderedDialog.newInstance(order).apply {
            onSkipOrder = {
                Log.d("HomeFragment", "User skip order")
                this@apply.dismiss()
            }
            onAcceptOrder = {
                Log.d("HomeFragment", "User accept order")
                this@apply.dismiss()
                requireContext().startActivity<OrderDetailActivity> {
                    putExtra("extra_order", order)
                }
            }
        }.show(parentFragmentManager, "NewOrderedDialog")
        Log.d("HomeFragment", "Dialog đã được show")
    }


    private fun updateUIState(state: ConnectionState) {
        binding.apply {
            constrainLayout1.background = requireContext().getDrawable(state.backgroundRes)
            updateConnectionStatusViews(state)
            llFinding.visibility = state.findingVisibility
            llSmallTrick.visibility = state.trickVisibility
            llFindingOrders.visibility = state.findingOrdersVisibility
        }
    }

    private fun updateConnectionStatusViews(state: ConnectionState) {
        when (state) {
            ConnectionState.CONNECTED -> {
                binding.ivConnectionStatus.setImageResource(R.drawable.ic_has_wifi)
                binding.ivToggleConnection.setImageResource(R.drawable.ic_green_power)
                binding.ivToggleConnection.setBackgroundResource(R.drawable.bg_border_white_16)
                binding.tvConnectionStatus.text = getString(R.string.ang_online)
            }

            ConnectionState.DISCONNECTED -> {
                binding.ivConnectionStatus.setImageResource(R.drawable.ic_no_wifi)
                binding.ivToggleConnection.setImageResource(R.drawable.ic_white_power)
                binding.ivToggleConnection.setBackgroundResource(R.drawable.bg_circle_stroke_gray_16)
                binding.tvConnectionStatus.text = getString(R.string.offline)
            }
        }
    }
}
