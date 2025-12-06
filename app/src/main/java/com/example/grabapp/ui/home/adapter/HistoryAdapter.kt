package com.example.grabapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grabapp.data.model.order.OrderItem
import com.example.grabapp.databinding.ItemHistoryBinding
import com.example.grabapp.extention.formatToVietNamTime

class HistoryAdapter(
    private var listOrderItem: List<OrderItem>,
    private val onCLick: (OrderItem) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryAdapter.ViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryAdapter.ViewHolder, position: Int) {
        holder.onHolder(position)
    }

    fun setListOrders(list: List<OrderItem>) {
        this.listOrderItem = list
        notifyDataSetChanged()
    }

    override fun getItemCount() = listOrderItem.size

    inner class ViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onHolder(position: Int) {
            val orderItem = listOrderItem[position]
            binding.apply {
                tvAddress.text = orderItem.pickupAddress.detail
                tvCost.text = orderItem.totalAmount.toInt().toString() + ".000₫"
                tvDate.text = orderItem.createdAt.formatToVietNamTime()
            }
            binding.root.setOnClickListener {
                onCLick(orderItem)
            }
        }
    }
}
