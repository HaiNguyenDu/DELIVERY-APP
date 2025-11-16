package com.example.grabapp.driver.register.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grabapp.databinding.ItemGenderBinding

class GenderAdapter(
    private val items: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<GenderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGenderBinding.inflate(
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
        private val binding: ItemGenderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(gender: String) {
            binding.tvGender.text = gender
            binding.root.setOnClickListener {
                onItemClick(gender)
            }
        }
    }
}

