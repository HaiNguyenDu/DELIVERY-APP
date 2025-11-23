package com.example.grabapp.data.repository

import com.example.grabapp.data.api.OderApi
import com.example.grabapp.domain.model.order.OrderItem
import com.example.grabapp.domain.repository.OrderRepository
import com.example.grabapp.network.ApiProvider

class OderRepositoryImpl : OrderRepository {
    private val orderApi = ApiProvider.getInstance().getAuthRequiredApi(OderApi::class.java)

    override suspend fun getListOrder(
        page: Int,
        size: Int,
        sort: String
    ): Result<List<OrderItem>> {
        try {
            val data = orderApi.getListOrders(
                page, size, sort
            )
            if (data.isSuccessful && data.body() != null) {
                val listItem = data.body()?.content
                return Result.success(listItem?:emptyList())
            }
            return Result.failure(Exception("Fail To Get Data"))
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun getOrderDetail(orderId: String): OrderItem? {
        try {
            val response = orderApi.getDetailOrder(orderId)
            if (response.isSuccessful && response.body() != null)
                return response.body()!!
            return null
        } catch (e: Exception) {
            return null
        }
    }
}