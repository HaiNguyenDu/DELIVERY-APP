package com.example.grabapp.model

enum class PeriodIncome(
    val periodIncomeType: String,
    val income: Int
) {
    DAILY("Thu nhập hôm nay", 100000),
    WEEKLY("Thu nhập tuần nay", 2000000),
    MONTHLY("Thu nhập tháng nay", 7000000)
}
