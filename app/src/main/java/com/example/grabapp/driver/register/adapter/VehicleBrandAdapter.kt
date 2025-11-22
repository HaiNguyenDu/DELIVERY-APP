package com.example.grabapp.driver.register.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grabapp.databinding.ItemVehicleBrandBinding
import com.example.grabapp.model.VehicleBrand

class VehicleBrandAdapter(
    private val items: List<VehicleBrand>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<VehicleBrandAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVehicleBrandBinding.inflate(
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

    inner class ViewHolder(
        private val binding: ItemVehicleBrandBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(vehicleBrand: VehicleBrand) {
            binding.tvVehicleBrand.text = vehicleBrand.brandName
            binding.root.setOnClickListener {
                onItemClick(vehicleBrand.brandName)
            }
        }
    }
}

