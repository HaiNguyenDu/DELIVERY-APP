package com.example.grabapp.driver.register.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grabapp.databinding.ItemDrivingLicenseBinding

class DrivingLicenseAdapter(
    private val items: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<DrivingLicenseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDrivingLicenseBinding.inflate(
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
        private val binding: ItemDrivingLicenseBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: String) {
            binding.tvDrivingLicense.text = category
            binding.root.setOnClickListener {
                onItemClick(category)
            }
        }
    }
}

