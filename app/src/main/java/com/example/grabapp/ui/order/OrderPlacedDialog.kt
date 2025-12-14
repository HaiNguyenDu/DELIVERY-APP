package com.example.grabapp.ui.order

import OrderStatus
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.grabapp.R
import com.example.grabapp.data.local.AppDatabase
import com.example.grabapp.data.model.order.DriverResponse
import com.example.grabapp.databinding.DialogBottomOrderPlacedBinding
import com.example.grabapp.extention.getTime
import com.example.grabapp.ui.home.MainViewModel
import com.example.grabapp.ui.home.MainViewModelFactory
import com.example.grabapp.ui.login.DetailOrderItemHistoryAdapter
import com.example.grabapp.utils.CurrentOrder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.maplibre.android.MapLibre
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class OrderPlacedDialog : BottomSheetDialogFragment() {
    private var _binding: DialogBottomOrderPlacedBinding? = null
    private val binding get() = _binding!!

    private var driverInfo: DriverResponse? = null
    fun add30Minutes(time: String): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val localTime = LocalTime.parse(time, formatter)

        val newTime = localTime.plusMinutes(30)

        return newTime.format(formatter)
    }

    private fun observerView() {
        binding.btnBack.setOnClickListener {
            dismiss()
        }
    }

    override fun dismiss() {
        super.dismiss()
        viewModel.isLoop = false
    }
    private fun observerData() {
        lifecycleScope.launch {
            viewModel.currentOrder.collect {
                if (it == null) return@collect
                if (it.status != OrderStatus.FINDING_DRIVER)
                    handleDriverInfo(it.shipperId ?: "")
                val detailOrderItemAdapter =
                    DetailOrderItemHistoryAdapter(it.packages)
                binding.rcvOrder.apply {
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = detailOrderItemAdapter
                    isNestedScrollingEnabled = false
                }
                val timeStart = it.createdAt.getTime()
                val timeEnd = add30Minutes(timeStart)
                binding.tvTimeRange.text = "$timeStart - $timeEnd"
                it.let {
                    binding.tvStatusText.text = it.status.label
                    when (it.status) {
                        OrderStatus.FINDING_DRIVER -> setLineStatus(binding.line1, true, false)
                        OrderStatus.DRIVER_ASSIGNED -> {
                            setLineStatus(binding.line1, true, false)
                        }

                        OrderStatus.DRIVER_EN_ROUTE_PICKUP -> {
                            setLineStatus(binding.line1, false, true)
                            setLineStatus(binding.line2, true, false)
                            binding.ivStep3.imageTintList = ContextCompat.getColorStateList(
                                requireContext(), R.color.green
                            )
                        }

                        OrderStatus.PACKAGE_PICKED -> {
                            setLineStatus(binding.line1, false, true)
                            setLineStatus(binding.line2, false, true)
                            setLineStatus(binding.line3, true, false)
                            binding.ivStep4.imageTintList = ContextCompat.getColorStateList(
                                requireContext(), R.color.green
                            )
                        }

                        OrderStatus.DELIVERED -> {
                            dismiss()
                            CurrentOrder.setOrderId("")
                        }

                        OrderStatus.ORDER_CANCELLED,
                        OrderStatus.RETURNED,
                        OrderStatus.DELIVERY_FAILED,
                        OrderStatus.PICKUP_FAILED -> {
                            dismiss()
                        }

                        else -> {
                            setLineStatus(binding.line1, false, true)
                            setLineStatus(binding.line2, false, true)
                            setLineStatus(binding.line3, true, false)
                            binding.ivStep4.imageTintList = ContextCompat.getColorStateList(
                                requireContext(), R.color.green
                            )
                        }
                    }

                }
            }
        }
    }

    suspend fun handleDriverInfo(diverId: String) {
        if (binding.layoutInfoDriver.isVisible) return
        val driverInfo = viewModel.getDriverInfo(diverId) ?: return
        binding.layoutInfoDriver.isVisible = true
        binding.tvDriverName.text = driverInfo.identityFullName
        if (driverInfo.avatarUrl?.isNotEmpty() ?: false)
            Glide.with(requireContext()).load(driverInfo.avatarUrl).into(binding.ivAvatar)
        binding.tvVehicleInfo.text = driverInfo.vehiclePlateNumber.toString()
        binding.tvRating.text = "${driverInfo.ratingAvg} ★"
        this.driverInfo = driverInfo
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        MapLibre.getInstance(requireContext())
        _binding = DialogBottomOrderPlacedBinding.inflate(inflater, container, false)
        val userDao = AppDatabase.getInstance(requireContext()).userDao()
        val factory = MainViewModelFactory(userDao, requireActivity().application)
        viewModel = ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]
        viewModel.isLoop = true
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.isLoop = false
    }

    private fun setupFullHeight() {
        dialog?.setOnShowListener { dialogInterface ->
            val bottomSheet =
                (dialogInterface as BottomSheetDialog)
                    .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
                behavior.isDraggable = false
                it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                it.setBackgroundColor(requireContext().getColor(R.color.white))
                dialogInterface.window?.let { window ->
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFullHeight()
        observerData()
        handleIntent()
        observerView()
    }

    private fun handleIntent() {
        val orderId = arguments?.getString("orderId") ?: CurrentOrder.orderID.value
        viewModel.startPolling(orderId)
    }

    private fun setLineStatus(
        progressBar: ProgressBar, isRunning: Boolean, isCompleted: Boolean = true
    ) {
        if (isRunning) {
            progressBar.isIndeterminate = true
            progressBar.indeterminateTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.green)
        } else {
            progressBar.isIndeterminate = false
            progressBar.max = 100

            if (isCompleted) {
                progressBar.progress = 100
                progressBar.progressTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.green)
            } else {
                progressBar.progress = 100
                progressBar.progressTintList =
                    ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)
            }
        }
    }

    companion object {
        fun getInstance(orderId: String): OrderPlacedDialog {
            val fragment = OrderPlacedDialog()
            val args = Bundle()
            args.putString("orderId", orderId)
            fragment.arguments = args
            return fragment
        }
    }
}