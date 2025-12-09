package com.example.grabapp.driver.order_detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grabapp.R
import com.example.grabapp.databinding.ItemDeliveryAddress2Binding
import com.example.grabapp.model.DeliveryAddressItem
import com.example.grabapp.model.OrderStatus
import com.example.grabapp.model.PackageStatus

class DeliveryAddressAdapter2(
    private val items: List<DeliveryAddressItem>,
    private val orderStatus: OrderStatus?,
    private val onCallClick: (String) -> Unit,
    private val onMessageClick: (String) -> Unit,
    private val onDeliveredClick: (DeliveryAddressItem) -> Unit
) : RecyclerView.Adapter<DeliveryAddressAdapter2.ViewHolder>() {
    
    /**
     * Tìm package đang được giao (package đầu tiên có status PICKED_UP hoặc DELIVERY_IN_PROGRESS)
     */
    private fun getCurrentDeliveryPackageIndex(): Int? {
        return items.indexOfFirst { item ->
            !item.isPickup && (item.packageStatus == PackageStatus.PICKED_UP || 
                              item.packageStatus == PackageStatus.DELIVERY_IN_PROGRESS)
        }.takeIf { it >= 0 }
    }

    class ViewHolder(
        private val binding: ItemDeliveryAddress2Binding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: DeliveryAddressItem,
            position: Int,
            orderStatus: OrderStatus?,
            currentDeliveryIndex: Int?,
            onCallClick: (String) -> Unit,
            onMessageClick: (String) -> Unit,
            onDeliveredClick: (DeliveryAddressItem) -> Unit
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
                
                // Handle tvDelivered visibility and text
                updateDeliveredButton(item, position, orderStatus, currentDeliveryIndex)
                
                tvDelivered.setOnClickListener {
                    onDeliveredClick(item)
                }
            }
        }
        
        private fun updateDeliveredButton(
            item: DeliveryAddressItem, 
            position: Int,
            orderStatus: OrderStatus?,
            currentDeliveryIndex: Int?
        ) {
            binding.apply {
                when {
                    item.isPickup -> {
                        // Pickup address logic
                        when (orderStatus) {
                            OrderStatus.DRIVER_ASSIGNED -> {
                                tvDelivered.visibility = View.VISIBLE
                                tvDelivered.text = "Đến lấy đơn"
                            }
                            OrderStatus.DRIVER_EN_ROUTE_PICKUP -> {
                                tvDelivered.visibility = View.VISIBLE
                                tvDelivered.text = "Đã đến điểm lấy"
                            }
                            OrderStatus.ARRIVED_PICKUP -> {
                                tvDelivered.visibility = View.VISIBLE
                                tvDelivered.text = OrderStatus.PACKAGE_PICKED.statusName
                            }
                            OrderStatus.PACKAGE_PICKED -> {
                                tvDelivered.visibility = View.GONE
                            }
                            else -> {
                                tvDelivered.visibility = View.GONE
                            }
                        }
                    }
                    else -> {
                        // Dropoff address logic - chỉ hiển thị cho package đang được giao
                        val isCurrentDeliveryPackage = currentDeliveryIndex != null && position == currentDeliveryIndex
                        
                        if (isCurrentDeliveryPackage) {
                            when (item.packageStatus) {
                                PackageStatus.PICKED_UP -> {
                                    tvDelivered.visibility = View.VISIBLE
                                    tvDelivered.text = "Đến giao hàng"
                                }
                                PackageStatus.DELIVERY_IN_PROGRESS -> {
                                    tvDelivered.visibility = View.VISIBLE
                                    tvDelivered.text = "Giao hàng"
                                }
                                PackageStatus.DELIVERED -> {
                                    tvDelivered.visibility = View.GONE
                                }
                                else -> {
                                    tvDelivered.visibility = View.GONE
                                }
                            }
                        } else {
                            // Không phải package đang được giao -> ẩn button
                            tvDelivered.visibility = View.GONE
                        }
                    }
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
        val currentDeliveryIndex = getCurrentDeliveryPackageIndex()
        holder.bind(
            items[position], 
            position, 
            orderStatus, 
            currentDeliveryIndex,
            onCallClick, 
            onMessageClick, 
            onDeliveredClick
        )
    }

    override fun getItemCount(): Int = items.size
}
