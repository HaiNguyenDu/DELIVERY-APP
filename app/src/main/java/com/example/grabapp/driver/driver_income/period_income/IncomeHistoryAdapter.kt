package com.example.grabapp.driver.driver_income.period_income

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grabapp.databinding.ItemIncomeHistoryBinding
import com.example.grabapp.model.IncomeHistory

class IncomeHistoryAdapter(
    private val items: List<IncomeHistory>
) : RecyclerView.Adapter<IncomeHistoryAdapter.IncomeHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeHistoryViewHolder {
        val binding = ItemIncomeHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IncomeHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IncomeHistoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class IncomeHistoryViewHolder(
        private val binding: ItemIncomeHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(incomeHistory: IncomeHistory) {
            binding.apply {
                tvTime.text = incomeHistory.time
                tvIncome.text = "${incomeHistory.income} VNĐ"
            }
        }
    }
}
