package com.example.grabapp.ui.address_selection.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grabapp.databinding.ItemTypePackageBinding
import com.example.grabapp.domain.enum.PackageTypeEnum

class PackageTypeAdapter : RecyclerView.Adapter<PackageTypeAdapter.ViewHolder>() {
    private var selectedPosition = 0
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemTypePackageBinding.inflate(
            android.view.LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    fun getSelectedType(): PackageTypeEnum{
        return PackageTypeEnum.entries[selectedPosition]
    }
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.onHolder(position)
    }

    override fun getItemCount() = PackageTypeEnum.entries.size

    inner class ViewHolder(private val binding: ItemTypePackageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onHolder(position: Int) {
            val type = PackageTypeEnum.entries[position]
            binding.tv.text = type.value
            binding.tv.setOnClickListener {
                val oldPosition = selectedPosition
                selectedPosition = position
                notifyItemChanged(oldPosition)
                notifyItemChanged(selectedPosition)
            }
            binding.tv.isChecked = position == selectedPosition
        }
    }
}