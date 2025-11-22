package com.example.grabapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grabapp.R
import com.example.grabapp.databinding.ItemQcBinding
import com.example.grabapp.model.ItemQC

class ADSAdapter : RecyclerView.Adapter<ADSAdapter.ViewHolder>() {
    companion object {
        val listData = listOf(
            ItemQC(R.drawable.qc_1, "Bật cảm hứng mỗi ngày", "QC", "Mailisa"),
            ItemQC(R.drawable.qc_3, "Khám phá phong cách riêng", "QC", "Poscher"),
            ItemQC(R.drawable.qc_4, "Tự tin theo đuổi đam mê", "QC", "Vinfast"),
            ItemQC(R.drawable.qc_5, "Nâng tầm trải nghiệm sống", "QC", "Katinat"),
            ItemQC(R.drawable.qc_6, "Sống chất – sống hết mình", "QC", "Lamboghini"),
        )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ADSAdapter.ViewHolder {
        val binding = ItemQcBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ADSAdapter.ViewHolder, position: Int) {
        holder.onHolder(position)
    }

    override fun getItemCount() = 5

    inner class ViewHolder(private val binding: ItemQcBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onHolder(position: Int) {
            val item = listData[position]
            binding.img.setImageResource(item.image)
            binding.tvBranch.text = item.branch
            binding.tvTitle.text = item.title
            binding.tvQc.text = item.qc
        }
    }
}