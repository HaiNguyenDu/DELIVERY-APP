package com.example.grabapp.ui.address_selection.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grabapp.databinding.ItemDetailOrderBinding
import com.example.grabapp.domain.model.order.PackageItemModel

class DetailOrderItemAdapter(
    var listOrder: List<PackageItemModel>
) : RecyclerView.Adapter<DetailOrderItemAdapter.ViewHolder>() {

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

    fun setListOrder(newList: List<PackageItemModel>, position: Int) {
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
        fun onHolder(packageItemModel: PackageItemModel) {
            binding.apply {
                if (packageItemModel.dropOffAddress.detail.isNotEmpty())
                    tvDrAddress.text = packageItemModel.dropOffAddress.detail
                if (packageItemModel.dropOffAddress.name.isNotEmpty())
                    tvDrUsername.text = packageItemModel.dropOffAddress.name
                if (packageItemModel.dropOffAddress.phone.isNotEmpty())
                    tvDrPhone.text = packageItemModel.dropOffAddress.phone
                if (packageItemModel.weightKg != 0.0)
                    tvDetailPackage.text = packageItemModel.toString()
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