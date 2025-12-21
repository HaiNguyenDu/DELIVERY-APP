package com.example.grabapp.ui.home.fragment

import android.os.Bundle
import android.view.View
import androidx.core.graphics.Insets
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentHistoryBinding
import com.example.grabapp.ui.home.MainViewModel
import com.example.grabapp.ui.home.adapter.HistoryAdapter
import com.example.grabapp.ui.order.OrderDetailDialog
import com.example.grabapp.ui.order.OrderPlacedDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class HistoryFragment : BaseFragment<FragmentHistoryBinding, MainViewModel>() {
    private lateinit var historyAdapter: HistoryAdapter
    override fun getLazyBinding(): Lazy<FragmentHistoryBinding> = lazy {
        FragmentHistoryBinding.inflate(layoutInflater)
    }

    override fun getLazyViewModel(): Lazy<MainViewModel> = lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.listOrder.collect {
                if (it.isEmpty()) {
                    binding.empty.visibility = View.VISIBLE
                    binding.rcv.visibility = View.GONE
                } else {
                    binding.empty.visibility = View.GONE
                    binding.rcv.visibility = View.VISIBLE
                    historyAdapter.setListOrders(it)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.isLoading.collect {
                if (it)
                    binding.loading.visibility = View.VISIBLE
                else
                    binding.loading.visibility = View.GONE
            }
        }
    }

    override fun setUpClick() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeData()
    }

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        view.setPadding(inset.left, inset.top, inset.right, 0)

    }

    private fun initView() {
        historyAdapter = HistoryAdapter(emptyList()) { order ->
            when {
                !(order.status?.isTerminal()?:true) -> {
                    val existingFragment = childFragmentManager.findFragmentByTag("placed")

                    if (existingFragment == null) {
                        OrderPlacedDialog.getInstance(order.id).show(childFragmentManager, "placed")
                    } else {
                        (existingFragment as? BottomSheetDialogFragment)?.dismissAllowingStateLoss()
                        OrderPlacedDialog.getInstance(order.id).show(childFragmentManager, "placed")
                    }
                }

                else -> {
                    OrderDetailDialog().setPosition(order.id).show(childFragmentManager, "ll")
                }
            }
        }
        binding.rcv.adapter = historyAdapter
        binding.rcv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }
}