package com.example.grabapp.driver.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.facebook.shimmer.Shimmer
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment<FragmentHomeBinding, DriverHomeViewModel>() {

    private var connectionState: ConnectionState = ConnectionState.DISCONNECTED
    private var countdownJob: Job? = null
    private var shimmerHandler: Handler? = null
    private var shimmerRunnable: Runnable? = null

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
        stopShimmer()
    }

    private fun showNewOrderDialog() {
        val order = Order.getMockOrder()
        showNewOrderDialogWithOrder(order)
    }

    private fun showNewOrderDialogWithOrder(order: Order) {
        val existingDialog = parentFragmentManager.findFragmentByTag("NewOrderedDialog")
        if (existingDialog != null && existingDialog.isAdded) {
            return
        }

        NewOrderedDialog.newInstance(order).apply {
            onSkipOrder = {
                this@apply.dismiss()
            }
            onAcceptOrder = { acceptedOrder ->
                this@apply.dismiss()
                requireContext().startActivity<OrderDetailActivity> {
                    putExtra("extra_order", acceptedOrder)
                }
            }
        }.show(parentFragmentManager, "NewOrderedDialog")
    }


    private fun updateUIState(state: ConnectionState) {
        binding.apply {
            constrainLayout1.background = requireContext().getDrawable(state.backgroundRes)
            updateConnectionStatusViews(state)
            llFinding.visibility = state.findingVisibility
            llSmallTrick.visibility = state.trickVisibility
            llFindingOrders.visibility = state.findingOrdersVisibility

            if (state == ConnectionState.CONNECTED) {
                setupShimmer()
            } else {
                stopShimmer()
            }
        }
    }

    private fun setupShimmer() {
        val shimmerBuilder = Shimmer.AlphaHighlightBuilder()
            .setBaseAlpha(1f)
            .setHighlightAlpha(0.08f)
            .setTilt(30f)
            .setDropoff(0.5f)
            .setDuration(1500L)
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setRepeatCount(0)
            .setShape(Shimmer.Shape.LINEAR)

        binding.shimmerLayout.setShimmer(shimmerBuilder.build())

        shimmerHandler = Handler(Looper.getMainLooper())
        shimmerRunnable = object : Runnable {
            override fun run() {
                binding.shimmerLayout.setShimmer(shimmerBuilder.build())
                binding.shimmerLayout.startShimmer()
                shimmerHandler?.postDelayed({
                    binding.shimmerLayout.stopShimmer()
                }, 1200L)
                shimmerHandler?.postDelayed(this, 3000L)
            }
        }

        shimmerHandler?.postDelayed(shimmerRunnable!!, 2000L)
    }

    private fun stopShimmer() {
        shimmerHandler?.removeCallbacks(shimmerRunnable ?: return)
        binding.shimmerLayout.stopShimmer()
        shimmerHandler = null
        shimmerRunnable = null
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
