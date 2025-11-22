package com.example.grabapp.ui.address_selection.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grabapp.databinding.ItemHistoryNearBinding

class HistoryNearAdapter : RecyclerView.Adapter<HistoryNearAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryNearAdapter.ViewHolder {
        val binding =
            ItemHistoryNearBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryNearAdapter.ViewHolder, position: Int) {

    }

    override fun getItemCount() = 10

    inner class ViewHolder(binding: ItemHistoryNearBinding) : RecyclerView.ViewHolder(binding.root)

}