package com.example.grabapp.driver.home.wallet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.grabapp.R
import com.example.grabapp.databinding.ItemTransactionHistoryBinding
import com.example.grabapp.model.TransactionHistory

class TransactionHistoryAdapter(
    private val items: List<TransactionHistory>
) : RecyclerView.Adapter<TransactionHistoryAdapter.TransactionHistoryViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionHistoryViewHolder {
        val binding = ItemTransactionHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionHistoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class TransactionHistoryViewHolder(
        private val binding: ItemTransactionHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: TransactionHistory) {
            binding.apply {
                when (transaction.type) {
                    TransactionHistory.TransactionType.INCOME -> {
                        ivTransactionIcon.setImageResource(R.drawable.ic_income)
                        tvTransactionType.text = binding.root.context.getString(R.string.ti_n_v_o)
                    }

                    TransactionHistory.TransactionType.WITHDRAW -> {
                        ivTransactionIcon.setImageResource(R.drawable.ic_withdraw)
                        tvTransactionType.text = binding.root.context.getString(R.string.ti_n_ra)
                    }
                }

                val amountText = when (transaction.type) {
                    TransactionHistory.TransactionType.INCOME -> {
                        "+${String.format("%,d", transaction.amount)}đ"
                    }

                    TransactionHistory.TransactionType.WITHDRAW -> {
                        "-${String.format("%,d", transaction.amount)}đ"
                    }
                }
                tvAmount.text = amountText

                val amountColor = when (transaction.type) {
                    TransactionHistory.TransactionType.INCOME -> {
                        ContextCompat.getColor(binding.root.context, R.color.green_36)
                    }

                    TransactionHistory.TransactionType.WITHDRAW -> {
                        ContextCompat.getColor(binding.root.context, R.color.red_51)
                    }
                }
                tvAmount.setTextColor(amountColor)

                tvDescription.text = transaction.description
                tvTime.text = transaction.time
            }
        }
    }
}
