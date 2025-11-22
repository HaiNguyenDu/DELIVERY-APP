package com.example.grabapp.model

data class TransactionHistory(
    val type: TransactionType,
    val amount: Long,
    val time: String,
    val description: String
) {
    enum class TransactionType {
        INCOME,
        WITHDRAW
    }

    companion object {
        fun getMockTransactions(): List<TransactionHistory> {
            return listOf(
                TransactionHistory(
                    type = TransactionType.INCOME,
                    amount = 75000L,
                    time = "14/06/2025, 14:24",
                    description = "Thu nhập từ đơn hàng DH001234"
                ),
                TransactionHistory(
                    type = TransactionType.INCOME,
                    amount = 50000L,
                    time = "14/06/2025, 10:15",
                    description = "Thu nhập từ đơn hàng DH001235"
                ),
                TransactionHistory(
                    type = TransactionType.WITHDRAW,
                    amount = 200000L,
                    time = "13/06/2025, 16:30",
                    description = "Rút tiền về tài khoản ngân hàng"
                ),
                TransactionHistory(
                    type = TransactionType.INCOME,
                    amount = 100000L,
                    time = "13/06/2025, 08:45",
                    description = "Thu nhập từ đơn hàng DH001230"
                ),
                TransactionHistory(
                    type = TransactionType.WITHDRAW,
                    amount = 150000L,
                    time = "12/06/2025, 14:20",
                    description = "Rút tiền về tài khoản ngân hàng"
                ),
                TransactionHistory(
                    type = TransactionType.INCOME,
                    amount = 60000L,
                    time = "12/06/2025, 11:10",
                    description = "Thu nhập từ đơn hàng DH001228"
                )
            )
        }
    }
}
