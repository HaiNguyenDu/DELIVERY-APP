package com.example.grabapp.ui.login

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grabapp.data.model.order.PackageItemResponse
import com.example.grabapp.databinding.ItemDetailOrderBinding

class DetailOrderItemHistoryAdapter(
    var listOrder: List<PackageItemResponse>
) : RecyclerView.Adapter<DetailOrderItemHistoryAdapter.ViewHolder>() {

    private var listener: DetailOrderItemAdapterListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemDetailOrderBinding.inflate(
            android.view.LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    fun setListOrder(newList: List<PackageItemResponse>, position: Int) {
        listOrder = newList
        if (position != -1)
            notifyItemChanged(position)
        else notifyDataSetChanged()
    }

    fun setListener(listener: DetailOrderItemAdapterListener) {
        this.listener = listener
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.onHolder(listOrder[position])
    }

    override fun getItemCount(): Int {
        return listOrder.size
    }

    inner class ViewHolder(val binding: ItemDetailOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onHolder(packageItem: PackageItemResponse) {
            binding.apply {
                if (packageItem.dropoffAddress.detail.isNotEmpty())
                    tvDrAddress.text = packageItem.dropoffAddress.detail
                if (packageItem.dropoffAddress.name.isNotEmpty())
                    tvDrUsername.text = packageItem.dropoffAddress.name
                if (packageItem.dropoffAddress.phone.isNotEmpty())
                    tvDrPhone.text = packageItem.dropoffAddress.phone
                if (packageItem.weightKg != 0.0)
                    tvDetailPackage.text = packageItem.toString()
                tvDrAddress.setOnClickListener {
                    listener?.onAddressClick(adapterPosition)
                }
                tvDetailPackage.setOnClickListener {
                    listener?.onDetailPackageClick(adapterPosition)
                }
            }
        }
    }

    interface DetailOrderItemAdapterListener {
        fun onAddressClick(position: Int)
        fun onDeleteClick(position: Int)
        fun onDetailPackageClick(position: Int)
    }
}