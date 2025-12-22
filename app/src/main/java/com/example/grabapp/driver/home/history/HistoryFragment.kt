package com.example.grabapp.driver.home.history

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.graphics.Insets
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabapp.R
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.data.repository.AIServiceRepository
import com.example.grabapp.data.repository.FileRepository
import com.example.grabapp.data.repository.OrderRepository
import com.example.grabapp.databinding.FragmentHistoryBinding
import com.example.grabapp.driver.home.DriverHomeViewModel
import com.example.grabapp.driver.home.DriverHomeViewModelFactory
import com.example.grabapp.driver.home.adapter.OrderAdapter
import com.example.grabapp.driver.order_detail.OrderDetailActivity
import com.example.grabapp.extention.onClickWithScale
import com.example.grabapp.model.Order
import com.example.grabapp.model.OrderState
import com.example.grabapp.util.CurrencyFormatter.formatIncome
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HistoryFragment : BaseFragment<FragmentHistoryBinding, DriverHomeViewModel>() {

    private var allOrders: List<Order> = emptyList()
    private var orderStatusMap: Map<String, String> = emptyMap()
    private var currentFilter: FilterType = FilterType.ALL
    private lateinit var orderAdapter: OrderAdapter

    enum class FilterType {
        ALL,
        COMPLETED,
        DELIVERING,
        CANCELED
    }

    override fun getLazyBinding(): Lazy<FragmentHistoryBinding> =
        lazy { FragmentHistoryBinding.inflate(layoutInflater) }

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

    override fun setUpClick() {
        setupTabListeners()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeOrders()
        setupRecyclerView()
        updateTabBackgrounds()
        updateStatistics()
    }

    private fun observeOrders() {
        lifecycleScope.launch {
            viewModel.orders.collectLatest { orders ->
                allOrders = orders
                updateRecyclerView()
                updateStatistics()
            }
        }
        lifecycleScope.launch {
            viewModel.orderStatusMap.collectLatest { statusMap ->
                orderStatusMap = statusMap
                updateRecyclerView()
            }
        }
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(
            items = getFilteredOrders(),
            orderStatusMap = orderStatusMap,
            onItemClick = { order ->
                navigateToOrderDetail(order)
            }
        )

        binding.rvOrderHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
        }
    }

    private fun updateRecyclerView() {
        val filteredOrders = getFilteredOrders()
        orderAdapter = OrderAdapter(
            items = filteredOrders,
            orderStatusMap = orderStatusMap,
            onItemClick = { order ->
                navigateToOrderDetail(order)
            }
        )
        binding.rvOrderHistory.adapter = orderAdapter
    }
    
    private fun navigateToOrderDetail(order: Order) {
        viewModel.fetchOrderById(order.orderId) { fetchedOrder ->
            if (fetchedOrder != null) {
                requireContext().startActivity(
                    Intent(requireContext(), OrderDetailActivity::class.java).apply {
                        putExtra("extra_order", fetchedOrder)
                    }
                )
            }
        }
    }

    private fun setupTabListeners() {
        binding.apply {
            tvAll.onClickWithScale {
                filterOrders(FilterType.ALL)
            }

            tvCompleted.onClickWithScale {
                filterOrders(FilterType.COMPLETED)
            }

            tvDelivering.onClickWithScale {
                filterOrders(FilterType.DELIVERING)
            }

            tvCanceled.onClickWithScale {
                filterOrders(FilterType.CANCELED)
            }
        }
    }

    private fun filterOrders(filterType: FilterType) {
        currentFilter = filterType
        updateRecyclerView()
        updateTabBackgrounds()
    }

    private fun updateStatistics() {
        val totalOrders = allOrders.size
        val completedOrders = allOrders.count { order ->
            order.orderState == OrderState.DELIVERED || 
            orderStatusMap[order.orderId] == "DELIVERED_WITH_ISSUES"
        }
        val totalIncome = allOrders
            .filter { order ->
                order.orderState == OrderState.DELIVERED || 
                orderStatusMap[order.orderId] == "DELIVERED_WITH_ISSUES"
            }
            .sumOf { it.income }

        binding.apply {
            tvTotalOrders.text = totalOrders.toString()
            tvCompletedOrders.text = completedOrders.toString()
            tvIncome.text = formatIncome(totalIncome)
        }
    }

    private fun getFilteredOrders(): List<Order> {
        return when (currentFilter) {
            FilterType.ALL -> allOrders
            FilterType.COMPLETED -> allOrders.filter { order ->
                order.orderState == OrderState.DELIVERED || 
                orderStatusMap[order.orderId] == "DELIVERED_WITH_ISSUES"
            }
            FilterType.DELIVERING -> allOrders.filter { it.orderState == OrderState.DELIVERING }
            FilterType.CANCELED -> {
                // Filter bao gồm cả CANCELED và RETURNED
                allOrders.filter { order ->
                    val status = orderStatusMap[order.orderId]
                    order.orderState == OrderState.CANCELED
                }
            }
        }
    }

    private fun updateTabBackgrounds() {
        binding.apply {
            // Reset all backgrounds
            tvAll.background = null
            tvCompleted.background = null
            tvDelivering.background = null
            tvCanceled.background = null

            // Reset all text colors
            tvAll.setTextColor(requireContext().getColor(R.color.grey_45))
            tvCompleted.setTextColor(requireContext().getColor(R.color.grey_45))
            tvDelivering.setTextColor(requireContext().getColor(R.color.grey_45))
            tvCanceled.setTextColor(requireContext().getColor(R.color.grey_45))

            // Set selected tab background and text color
            when (currentFilter) {
                FilterType.ALL -> {
                    tvAll.setBackgroundResource(R.drawable.bg_border_white_6)
                    tvAll.setTextColor(requireContext().getColor(R.color.black))
                }

                FilterType.COMPLETED -> {
                    tvCompleted.setBackgroundResource(R.drawable.bg_border_white_6)
                    tvCompleted.setTextColor(requireContext().getColor(R.color.black))
                }

                FilterType.DELIVERING -> {
                    tvDelivering.setBackgroundResource(R.drawable.bg_border_white_6)
                    tvDelivering.setTextColor(requireContext().getColor(R.color.black))
                }

                FilterType.CANCELED -> {
                    tvCanceled.setBackgroundResource(R.drawable.bg_border_white_6)
                    tvCanceled.setTextColor(requireContext().getColor(R.color.black))
                }
            }
        }
    }

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        binding.emptyView.setPadding(0, inset.top, 0, 0)
        binding.textView1.setPadding(0, inset.top / 2, 0, 0)
    }
}
