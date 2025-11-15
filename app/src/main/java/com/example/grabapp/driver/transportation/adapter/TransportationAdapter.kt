package com.example.grabapp.driver.transportation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.grabapp.R
import com.example.grabapp.databinding.ItemTransportationBinding
import com.example.grabapp.model.Transportation

class TransportationAdapter(
    private val items: List<Transportation>,
    private val selectedTransportation: Transportation,
    private val onItemClick: (Transportation) -> Unit
) : RecyclerView.Adapter<TransportationAdapter.TransportationViewHolder>() {

    private var currentSelected: Transportation = selectedTransportation

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransportationViewHolder {
        val binding = ItemTransportationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransportationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransportationViewHolder, position: Int) {
        holder.bind(items[position], currentSelected == items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class TransportationViewHolder(
        private val binding: ItemTransportationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(transportation: Transportation, isSelected: Boolean) {
            binding.apply {
                tvTransportationName.text = root.context.getString(transportation.stringResId)

                if (isSelected) {
                    tvTransportationName.setTextColor(
                        ContextCompat.getColor(
                            root.context,
                            R.color.green_36
                        )
                    )
                    ivCheck.isVisible = true
                } else {
                    tvTransportationName.setTextColor(
                        ContextCompat.getColor(
                            root.context,
                            R.color.black
                        )
                    )
                    ivCheck.isInvisible = true
                }

                root.setOnClickListener {
                    onItemClick(transportation)
                }
            }
        }
    }
}

