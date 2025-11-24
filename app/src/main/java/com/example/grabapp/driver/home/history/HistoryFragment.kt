package com.example.grabapp.driver.home.history

import android.os.Bundle
import android.view.View
import androidx.core.graphics.Insets
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabapp.R
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.data.repository.AIServiceRepository
import com.example.grabapp.data.repository.FileRepository
import com.example.grabapp.databinding.FragmentHistoryBinding
import com.example.grabapp.driver.home.DriverHomeViewModel
import com.example.grabapp.driver.home.adapter.OrderAdapter
import com.example.grabapp.extention.onClickWithScale
import com.example.grabapp.model.Order
import com.example.grabapp.model.OrderState

class HistoryFragment : BaseFragment<FragmentHistoryBinding, DriverHomeViewModel>() {
    
    private var allOrders: List<Order> = emptyList()
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
            DriverHomeViewModel(requireActivity().application, fileRepository, aiServiceRepository)
        }

    override fun setUpClick() {
        setupTabListeners()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadOrders()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        updateTabBackgrounds()
    }

    private fun loadOrders() {
        allOrders = Order.getMockOrders()
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(items = getFilteredOrders())
        
        binding.rvOrderHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
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
        val filteredOrders = getFilteredOrders()
        orderAdapter = OrderAdapter(items = filteredOrders)
        binding.rvOrderHistory.adapter = orderAdapter
        updateTabBackgrounds()
    }

    private fun getFilteredOrders(): List<Order> {
        return when (currentFilter) {
            FilterType.ALL -> allOrders
            FilterType.COMPLETED -> allOrders.filter { it.orderState == OrderState.DELIVERED }
            FilterType.DELIVERING -> allOrders.filter { it.orderState == OrderState.DELIVERING }
            FilterType.CANCELED -> allOrders.filter { it.orderState == OrderState.CANCELED }
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
        binding.textView1.setPadding(0, inset.top/2, 0, 0)
    }
}
