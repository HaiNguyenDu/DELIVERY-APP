package com.example.grabapp.driver.home

import android.os.Bundle
import android.view.View
import androidx.core.graphics.Insets
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.R
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.data.repository.AIServiceRepository
import com.example.grabapp.data.repository.FileRepository
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
            DriverHomeViewModel(requireActivity().application, fileRepository, aiServiceRepository)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUIState(ConnectionState.DISCONNECTED)
    }

    override fun setUpClick() {
        binding.ivToggleConnection.onClickWithScale {
            connectionState = connectionState.toggle()
            updateUIState(connectionState)
            handleConnectionStateChange(connectionState)
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
            startCountdownToShowDialog()
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
        NewOrderedDialog().apply {
            onSkipOrder = {
                this@apply.dismiss()
                if (connectionState == ConnectionState.CONNECTED) {
                    startCountdownToShowDialog()
                }
            }
            onAcceptOrder = {
                this@apply.dismiss()
                requireContext().startActivity<OrderDetailActivity> {
                    putExtra("extra_order", order)
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
