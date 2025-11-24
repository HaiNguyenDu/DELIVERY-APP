package com.example.grabapp.domain.repository

import com.example.grabapp.domain.model.order.OrderForm
import com.example.grabapp.data.model.order.OrderItem

interface OrderRepository {
    suspend fun getListOrder(page: Int, size: Int, sort: String): Result<List<OrderItem>>
    suspend fun getOrderDetail(orderId: String): OrderItem?

    suspend fun createOrder(orderForm: OrderForm): Result<String>
}