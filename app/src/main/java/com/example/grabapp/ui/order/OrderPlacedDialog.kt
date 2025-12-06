package com.example.grabapp.ui.order

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.R
import com.example.grabapp.data.local.AppDatabase
import com.example.grabapp.databinding.DialogBottomOrderPlacedBinding
import com.example.grabapp.domain.enum.OrderStatus
import com.example.grabapp.extention.formatToVietNamTime
import com.example.grabapp.extention.getTime
import com.example.grabapp.ui.home.MainViewModel
import com.example.grabapp.ui.home.MainViewModelFactory
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

    private val fcmReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val status = intent?.getStringExtra("status") ?: ""
            val orderID = intent?.getStringExtra("orderID") ?: ""
        }
    }
    fun add30Minutes(time: String): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val localTime = LocalTime.parse(time, formatter)

        val newTime = localTime.plusMinutes(30)

        return newTime.format(formatter)
    }
    private fun observerData() {
        lifecycleScope.launch {
            viewModel.currentOrder.collect {
                val timeStart = it?.createdAt?.getTime()
                val timeEnd =  add30Minutes(timeStart?:"")
                binding.tvTimeRange.text = "$timeStart - $timeEnd"
                it?.let {
                    binding.tvStatusText.text = it.status.label
                    when (it.status) {
                        OrderStatus.FINDING_DRIVER -> setLineStatus(binding.line1, true, false)
                        OrderStatus.DRIVER_EN_ROUTE_PICKUP -> {
                            setLineStatus(binding.line1, false, true)
                            setLineStatus(binding.line2, true, false)
                        }
                        OrderStatus.PACKAGE_PICKED -> {
                            setLineStatus(binding.line1, false, true)
                            setLineStatus(binding.line2, false, true)
                            setLineStatus(binding.line3, true, false)
                        }
                        OrderStatus.DELIVERED -> {
                            dismiss()
                            CurrentOrder.setOrderId("")
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private var isRun = false
    private lateinit var viewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MapLibre.getInstance(requireContext())
        _binding = DialogBottomOrderPlacedBinding.inflate(inflater, container, false)
        val userDao = AppDatabase.getInstance(requireContext()).userDao()
        val factory = MainViewModelFactory(userDao, requireActivity().application)
        viewModel = ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]
        viewModel.isLoop = true
        viewModel.startPolling(CurrentOrder.orderID.value)
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
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFullHeight()
        observerData()
    }

    private fun setLineStatus(
        progressBar: ProgressBar,
        isRunning: Boolean,
        isCompleted: Boolean = true
    ) {
        if (isRunning) {
            progressBar.isIndeterminate = true
            progressBar.indeterminateTintList =
                ContextCompat.getColorStateList(requireContext(), android.R.color.holo_green_light)
        } else {
            progressBar.isIndeterminate = false
            progressBar.max = 100

            if (isCompleted) {
                progressBar.progress = 100
                progressBar.progressTintList = ContextCompat.getColorStateList(
                    requireContext(),
                    android.R.color.holo_green_dark
                )
            } else {
                progressBar.progress = 100
                progressBar.progressTintList =
                    ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)
            }
        }
    }
}