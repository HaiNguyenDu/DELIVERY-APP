package com.example.grabapp.model

data class OrderHistory(
    val orderId: String,
    val orderState: String,
    val orderTime: String,
    val pickerAddress: String,
    val deliveryAddress: String,
    val distance: String,
    val income: String
)
