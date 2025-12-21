package com.example.grabapp.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabapp.R
import com.example.grabapp.data.local.AppDatabase
import com.example.grabapp.databinding.DialogOrderDetailBinding
import com.example.grabapp.extention.formatToVietNamTime
import com.example.grabapp.ui.home.MainViewModel
import com.example.grabapp.ui.home.MainViewModelFactory
import com.example.grabapp.ui.login.DetailOrderItemHistoryAdapter
import com.example.grabapp.ui.login.DetailOrderItemHistoryAdapter.DetailOrderItemAdapterListener
import com.example.grabapp.view.PreviewImageDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class OrderDetailDialog : BottomSheetDialogFragment() {
    private var _binding: DialogOrderDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    var orderId = ""

    fun setPosition(orderId: String): OrderDetailDialog {
        this.orderId = orderId
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogOrderDetailBinding.inflate(inflater, container, false)
        val userDao = AppDatabase.getInstance(requireContext()).userDao()
        val factory = MainViewModelFactory(userDao, requireActivity().application)
        viewModel = ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setupFullHeight()
    }

    fun initView() {
        binding.btnBack.setOnClickListener {
            dismiss()
        }
        viewModel.getSelectedOrder(orderId)
        lifecycleScope.launch {
            viewModel.selectedOrder.collect {
                binding.loading.visibility = View.GONE
                val detailOrderItemAdapter =
                    DetailOrderItemHistoryAdapter(it.packages)
                detailOrderItemAdapter.setListener(object : DetailOrderItemAdapterListener {
                    override fun onAddressClick(position: Int) {

                    }

                    override fun onDeleteClick(position: Int) {

                    }

                    override fun onDetailPackageClick(position: Int) {

                    }

                    override fun onPreviewImageClick(url: String) {
                        PreviewImageDialog.with(requireContext(), url).show()
                    }

                }
                )
                binding.rcvOrder.apply {
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = detailOrderItemAdapter
                    isNestedScrollingEnabled = false
                }
                binding.tvTotalPrice.text = it.totalAmount.toString() + "Đ"
                binding.tvTime.text = it.createdAt.formatToVietNamTime()
            }
        }
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
}