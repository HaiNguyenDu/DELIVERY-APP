package com.example.grabapp.util

import java.util.Locale

object CurrencyFormatter {
    
    /**
     * Format tiền theo yêu cầu:
     * - Dưới 1000: "108đ"
     * - Từ 1000 đến dưới 1 triệu: "108k" (trăm nghìn)
     * - Từ 1 triệu đến dưới 1 tỷ: "1,2m" (triệu)
     * - Từ 1 tỷ trở lên: "1.2B" (tỷ)
     */
    fun formatIncome(amount: Long): String {
        return when {
            amount >= 1_000_000_000 -> {
                val billions = amount / 1_000_000_000.0
                String.format(Locale.US, "%.1fB", billions)
            }
            amount >= 1_000_000 -> {
                val millions = amount / 1_000_000.0
                String.format(Locale.getDefault(), "%.1fm", millions).replace(".", ",")
            }
            amount >= 1_000 -> {
                val thousands = amount / 1_000
                "${thousands}k"
            }
            else -> "${amount}đ"
        }
    }
}

