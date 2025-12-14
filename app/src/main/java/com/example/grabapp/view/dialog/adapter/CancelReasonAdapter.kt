package com.example.grabapp.view.dialog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.grabapp.R
import com.example.grabapp.databinding.ItemCancelReasonBinding
import com.example.grabapp.model.CancelOrderType

class CancelReasonAdapter(
    private val items: List<CancelOrderType>,
    private val onItemClick: (CancelOrderType) -> Unit
) : RecyclerView.Adapter<CancelReasonAdapter.CancelReasonViewHolder>() {

    private var selectedItem: CancelOrderType? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CancelReasonViewHolder {
        val binding = ItemCancelReasonBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CancelReasonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CancelReasonViewHolder, position: Int) {
        holder.bind(items[position], selectedItem == items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setSelectedItem(item: CancelOrderType?) {
        val previousSelected = selectedItem
        selectedItem = item
        previousSelected?.let { notifyItemChanged(items.indexOf(it)) }
        item?.let { notifyItemChanged(items.indexOf(it)) }
    }

    fun getSelectedItem(): CancelOrderType? = selectedItem

    inner class CancelReasonViewHolder(
        private val binding: ItemCancelReasonBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CancelOrderType, isSelected: Boolean) {
            binding.apply {
                tvReason.text = item.cancelName

                if (isSelected) {
                    root.background = ContextCompat.getDrawable(
                        root.context,
                        R.drawable.bg_circle_stroke_8
                    )
                } else {
                    root.background = null
                }

                root.setOnClickListener {
                    setSelectedItem(item)
                    onItemClick(item)
                }
            }
        }
    }
}
