package com.example.grabapp.driver.home.history

import android.os.Bundle
import android.view.View
import androidx.core.graphics.Insets
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentHistoryBinding
import com.example.grabapp.driver.home.DriverHomeViewModel
import com.example.grabapp.driver.home.adapter.OrderHistoryAdapter
import com.example.grabapp.model.OrderHistory

class HistoryFragment : BaseFragment<FragmentHistoryBinding, DriverHomeViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentHistoryBinding> =
        lazy { FragmentHistoryBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverHomeViewModel> =
        lazy { DriverHomeViewModel(requireActivity().application) }

    override fun setUpClick() = Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val mockOrderHistoryList = getMockOrderHistoryList()
        
        val orderHistoryAdapter = OrderHistoryAdapter(
            items = mockOrderHistoryList
        )

        binding.rvOrderHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderHistoryAdapter
        }
    }

    private fun getMockOrderHistoryList(): List<OrderHistory> {
        return listOf(
            OrderHistory(
                orderId = "DH001234",
                orderState = "Hoàn thành",
                orderTime = "02/11/2025 05:20",
                fromAddress = "123 Đường Lê Lợi, Quận 1, TP.HCM",
                toAddress = "456 Đường Nguyễn Huệ, Quận 1, TP.HCM",
                orderDistance = "3.2 km",
                orderIncome = "+25.000đ"
            ),
            OrderHistory(
                orderId = "DH001235",
                orderState = "Hoàn thành",
                orderTime = "02/11/2025 08:15",
                fromAddress = "789 Đường Nguyễn Trãi, Quận 5, TP.HCM",
                toAddress = "321 Đường Võ Văn Tần, Quận 3, TP.HCM",
                orderDistance = "5.8 km",
                orderIncome = "+35.000đ"
            ),
            OrderHistory(
                orderId = "DH001236",
                orderState = "Hoàn thành",
                orderTime = "02/11/2025 10:30",
                fromAddress = "555 Đường Điện Biên Phủ, Quận Bình Thạnh, TP.HCM",
                toAddress = "777 Đường Xô Viết Nghệ Tĩnh, Quận Bình Thạnh, TP.HCM",
                orderDistance = "2.5 km",
                orderIncome = "+20.000đ"
            ),
            OrderHistory(
                orderId = "DH001237",
                orderState = "Hoàn thành",
                orderTime = "02/11/2025 14:45",
                fromAddress = "999 Đường Cách Mạng Tháng 8, Quận 10, TP.HCM",
                toAddress = "111 Đường Lý Thường Kiệt, Quận 10, TP.HCM",
                orderDistance = "4.1 km",
                orderIncome = "+30.000đ"
            ),
            OrderHistory(
                orderId = "DH001238",
                orderState = "Hoàn thành",
                orderTime = "02/11/2025 16:20",
                fromAddress = "222 Đường Hoàng Văn Thụ, Quận Phú Nhuận, TP.HCM",
                toAddress = "333 Đường Phan Đình Phùng, Quận Phú Nhuận, TP.HCM",
                orderDistance = "1.8 km",
                orderIncome = "+18.000đ"
            )
        )
    }

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        binding.emptyView.setPadding(0, inset.top, 0, 0)
        binding.textView1.setPadding(0, inset.top/2, 0, 0)
    }
}
