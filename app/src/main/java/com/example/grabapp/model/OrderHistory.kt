package com.example.grabapp.model

data class OrderHistory(
    val orderId: String,
    val orderState: String,
    val orderTime: String,
    val fromAddress: String,
    val toAddress: String,
    val orderDistance: String,
    val orderIncome: String
)
