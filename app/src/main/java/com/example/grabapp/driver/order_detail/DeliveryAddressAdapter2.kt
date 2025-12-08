package com.example.grabapp.driver.order_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grabapp.R
import com.example.grabapp.databinding.ItemDeliveryAddress2Binding
import com.example.grabapp.view.dialog.DeliveryAddressItem

class DeliveryAddressAdapter2(
    private val items: List<DeliveryAddressItem>,
    private val onCallClick: (String) -> Unit,
    private val onMessageClick: (String) -> Unit
) : RecyclerView.Adapter<DeliveryAddressAdapter2.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemDeliveryAddress2Binding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: DeliveryAddressItem,
            onCallClick: (String) -> Unit,
            onMessageClick: (String) -> Unit
        ) {
            binding.apply {
                tvDeliveryType.text = if (item.isPickup) {
                    root.context.getString(R.string.i_m_l_y_h_ng)
                } else {
                    root.context.getString(R.string.i_m_giao_h_ng)
                }

                tvDeliveryName.text = item.name
                tvDeliveryAddress.text = item.address

                ivIcon.setBackgroundResource(
                    if (item.isPickup) {
                        R.drawable.bg_gradient_finding_orders_100
                    } else {
                        R.drawable.bg_gradient_red_100
                    }
                )

                llCall.setOnClickListener {
                    onCallClick(item.name)
                }

                llMess.setOnClickListener {
                    onMessageClick(item.name)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDeliveryAddress2Binding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], onCallClick, onMessageClick)
    }

    override fun getItemCount(): Int = items.size
}
