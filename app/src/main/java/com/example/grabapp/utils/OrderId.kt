package com.example.grabapp.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object CurrentOrder {
    private val _orderID = MutableStateFlow<String>("")
    val orderID: StateFlow<String> = _orderID

    fun setOrderId(orderID:String)
    {
        _orderID.value = orderID
    }

    fun currentOrderId(): String {
        return orderID.value
    }

}