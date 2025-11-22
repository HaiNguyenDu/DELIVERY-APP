package com.example.grabapp.driver.register.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grabapp.databinding.ItemFuelBinding
import com.example.grabapp.model.FuelType

class FuelAdapter(
    private val items: List<FuelType>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<FuelAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFuelBinding.inflate(
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
        private val binding: ItemFuelBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(fuelType: FuelType) {
            binding.tvFuel.text = fuelType.fuelName
            binding.root.setOnClickListener {
                onItemClick(fuelType.fuelName)
            }
        }
    }
}

