package com.example.grabapp.driver.driver_income.income

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grabapp.databinding.LayoutIncomeBinding
import com.example.grabapp.extention.onClickWithScale
import com.example.grabapp.model.PeriodIncome

class IncomeAdapter(
    private val items: List<PeriodIncome>,
    private val onItemClick: (PeriodIncome) -> Unit
) : RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeViewHolder {
        val binding = LayoutIncomeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IncomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IncomeViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class IncomeViewHolder(
        private val binding: LayoutIncomeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(periodIncome: PeriodIncome) {
            binding.apply {
                tvIncomeType.text = periodIncome.periodIncomeType
                tvIncome.text = "${periodIncome.income} VNĐ"
                
                root.onClickWithScale {
                    onItemClick(periodIncome)
                }
            }
        }
    }
}
