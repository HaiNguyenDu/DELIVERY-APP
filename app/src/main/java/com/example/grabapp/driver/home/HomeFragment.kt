package com.example.grabapp.driver.home

import android.view.View
import androidx.core.graphics.Insets
import com.example.grabapp.R
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentHomeBinding
import com.example.grabapp.extention.onClickWithScale

class HomeFragment : BaseFragment<FragmentHomeBinding, DriverHomeViewModel>() {

    private var connectionState: ConnectionState = ConnectionState.DISCONNECTED

    override fun getLazyBinding(): Lazy<FragmentHomeBinding> =
        lazy { FragmentHomeBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverHomeViewModel> =
        lazy { DriverHomeViewModel(requireActivity().application) }

    override fun setUpClick() {
        initializeConnectionState()
        setupConnectionToggleListener()
    }

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        binding.emptyView.setPadding(0, inset.top, 0, 0)
        binding.llTitle.setPadding(0, inset.top / 2, 0, 0)
    }

    private fun initializeConnectionState() {
        updateUIState(ConnectionState.DISCONNECTED)
    }

    private fun setupConnectionToggleListener() {
        binding.ivToggleConnection.onClickWithScale {
            connectionState = connectionState.toggle()
            updateUIState(connectionState)
        }
    }

    private fun updateUIState(state: ConnectionState) {
        binding.apply {
            constrainLayout1.background = requireContext().getDrawable(state.backgroundRes)
            updateConnectionStatusViews(state)
            updateFindingSectionVisibility(state)
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

    private fun updateFindingSectionVisibility(state: ConnectionState) {
        binding.apply {
            llFinding.visibility = state.findingVisibility
            llSmallTrick.visibility = state.trickVisibility
            llFindingOrders.visibility = state.findingOrdersVisibility
        }
    }

    private enum class ConnectionState(
        val backgroundRes: Int,
        val findingVisibility: Int,
        val trickVisibility: Int,
        val findingOrdersVisibility: Int
    ) {
        CONNECTED(
            backgroundRes = R.drawable.bg_gradient_green_connected,
            findingVisibility = View.VISIBLE,
            trickVisibility = View.GONE,
            findingOrdersVisibility = View.VISIBLE
        ),
        DISCONNECTED(
            backgroundRes = R.drawable.bg_gradient_green_disconnected,
            findingVisibility = View.GONE,
            trickVisibility = View.VISIBLE,
            findingOrdersVisibility = View.GONE
        );

        fun toggle(): ConnectionState = when (this) {
            CONNECTED -> DISCONNECTED
            DISCONNECTED -> CONNECTED
        }
    }
}
