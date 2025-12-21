package com.example.grabapp.domain.repository

import OrderStatus
import com.example.grabapp.data.model.order.CreateOrderResponse
import com.example.grabapp.domain.model.order.OrderForm
import com.example.grabapp.data.model.order.OrderItem
import com.example.grabapp.data.model.order.OrderItemResponse
import com.example.grabapp.data.model.order.PriceRouteItem

interface OrderRepository {
    suspend fun getListOrder(page: Int, size: Int, sort: String): Result<List<OrderItem>>
    suspend fun getOrderDetail(orderId: String): OrderItemResponse?
    suspend fun createOrder(orderForm: OrderForm): Result<CreateOrderResponse>
    suspend fun getPriceAndRoute(orderForm: OrderForm): List<PriceRouteItem>

    suspend fun updateStatus(orderId: String, status: OrderStatus): Boolean
}