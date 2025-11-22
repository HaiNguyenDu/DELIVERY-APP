package com.example.grabapp.driver.register.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grabapp.databinding.ItemBankBinding
import com.example.grabapp.model.Bank

class BankAdapter(
    private val items: List<Bank>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<BankAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBankBinding.inflate(
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
        private val binding: ItemBankBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(bank: Bank) {
            binding.tvBank.text = bank.bankName
            binding.root.setOnClickListener {
                onItemClick(bank.bankName)
            }
        }
    }
}

