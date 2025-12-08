package com.example.grabapp.view.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grabapp.R
import com.example.grabapp.databinding.ItemDeliveryAddressBinding
import com.example.grabapp.model.DeliveryAddressItem

class DeliveryAddressAdapter(
    private val items: List<DeliveryAddressItem>
) : RecyclerView.Adapter<DeliveryAddressAdapter.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemDeliveryAddressBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DeliveryAddressItem) {
            binding.apply {
                deliveryType.text = if (item.isPickup) {
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
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDeliveryAddressBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
