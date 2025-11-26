package com.example.grabapp.ui.new_main.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.grabapp.databinding.ItemImageBinding

class ListImageAdapter(
    private val listUri: List<Uri>
) : RecyclerView.Adapter<ListImageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListImageAdapter.ViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.onHolder(position)
    }

    override fun getItemCount() = 10

    inner class ViewHolder(val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onHolder(position: Int) {
        }
    }

}