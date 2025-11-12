package com.example.grabapp.driver.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grabapp.databinding.ItemOrderHistoryBinding
import com.example.grabapp.model.OrderHistory

class OrderHistoryAdapter(
    private val items: List<OrderHistory>
) : RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val binding = ItemOrderHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class OrderHistoryViewHolder(
        private val binding: ItemOrderHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(orderHistory: OrderHistory) {
            binding.apply {
                tvOrderId.text = orderHistory.orderId
                tvOrderState.text = orderHistory.orderState
                tvTime.text = orderHistory.orderTime
                tvFromAddress.text = orderHistory.fromAddress
                tvToAddress.text = orderHistory.toAddress
                tvDistance.text = orderHistory.orderDistance
                tvIncome.text = orderHistory.orderIncome
            }
        }
    }
}
