package com.example.grabapp.driver.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grabapp.databinding.ItemOrderHistoryBinding
import com.example.grabapp.model.Order
import com.example.grabapp.model.OrderState
import com.example.grabapp.R

class OrderAdapter(
    private val items: List<Order>,
    private val orderStatusMap: Map<String, String> = emptyMap(),
    private val onItemClick: (Order) -> Unit
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
    
    fun updateItems(newItems: List<Order>) {
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    inner class OrderViewHolder(
        private val binding: ItemOrderHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.apply {
                tvOrderId.text = order.orderId
                tvOrderState.text = getOrderStateText(order.orderState, order.orderId)
                tvTime.text = order.orderTime
                tvFromAddress.text = order.pickerAddress
                tvToAddress.text = order.deliveryAddress
                tvDistance.text = order.distance
                tvIncome.text = "+${String.format("%,d", order.income)}đ"
                
                root.setOnClickListener {
                    onItemClick(order)
                }
            }
        }

        private fun getOrderStateText(orderState: OrderState, orderId: String): String {
            val actualStatus = orderStatusMap[orderId]
            if (actualStatus == "RETURNED") {
                return "Đã trả hàng"
            }
            
            return when (orderState) {
                OrderState.DELIVERED -> binding.root.context.getString(R.string.ho_n_th_nh)
                OrderState.DELIVERING -> binding.root.context.getString(R.string.ang_giao)
                OrderState.CANCELED -> binding.root.context.getString(R.string.h_y)
                OrderState.RECEIVED_ORDER -> binding.root.context.getString(R.string.received_order)
                OrderState.COMING_TO_PICKUP -> binding.root.context.getString(R.string.ang_n_l_y_h_ng)
                OrderState.RECEIVED_GOODS -> binding.root.context.getString(R.string.received_goods)
            }
        }
    }
}
