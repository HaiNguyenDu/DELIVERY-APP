package com.example.grabapp.driver.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grabapp.databinding.ItemOrderHistoryBinding
import com.example.grabapp.model.Order
import com.example.grabapp.model.OrderState
import com.example.grabapp.R

class OrderAdapter(
    private val items: List<Order>
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class OrderViewHolder(
        private val binding: ItemOrderHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.apply {
                tvOrderId.text = order.orderId
                tvOrderState.text = getOrderStateText(order.orderState)
                tvTime.text = order.orderTime
                tvFromAddress.text = order.pickerAddress
                tvToAddress.text = order.deliveryAddress
                tvDistance.text = order.distance
                tvIncome.text = "+${String.format("%,d", order.income)}đ"
            }
        }

        private fun getOrderStateText(orderState: OrderState): String {
            return when (orderState) {
                OrderState.DELIVERED -> binding.root.context.getString(R.string.ho_n_th_nh)
                OrderState.DELIVERING -> binding.root.context.getString(R.string.ang_giao)
                OrderState.CANCELED -> binding.root.context.getString(R.string.h_y)
                else -> ""
            }
        }
    }
}
