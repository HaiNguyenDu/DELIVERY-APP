package com.example.grabapp.data.repository

import com.example.grabapp.data.api.OderApi
import com.example.grabapp.data.model.order.OrderItem
import com.example.grabapp.data.model.order.OrderItemResponse
import com.example.grabapp.data.model.order.PriceRouteItem
import com.example.grabapp.domain.model.order.OrderForm
import com.example.grabapp.domain.model.order.toCreateOrderRequest
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
                return Result.success(listItem ?: emptyList())
            }
            return Result.failure(Exception("Fail To Get Data"))
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun getOrderDetail(orderId: String): OrderItemResponse? {
        try {
            val response = orderApi.getDetailOrder(orderId)
            if (response.isSuccessful && response.body() != null)
                return response.body()!!
            return null
        } catch (e: Exception) {
            return null
        }
    }

    override suspend fun createOrder(orderForm: OrderForm): Result<String> {
        val createOrderRequest =
            orderForm.toCreateOrderRequest()
        try {
            val response = orderApi.createOrder(
                createOrderRequest
            )
            if (response.isSuccessful && response.body()?.orderId != null)
                return Result.success(response.body()!!.orderId)
            return Result.failure(Exception("Fail To Create Order"))
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun getPriceAndRoute(orderForm: OrderForm): List<PriceRouteItem> {
        val createOrderRequest = orderForm.toCreateOrderRequest()
        try {
            val response = orderApi.getPriceRoute(createOrderRequest)
            if(response.isSuccessful) {
                val listItem = response.body()
                if (listItem != null)
                    return listItem
            }
        } catch (e: Exception) {

        }
        return emptyList()
    }

    companion object {
        private lateinit var orderRepository: OrderRepository
        fun getInstance(): OrderRepository {
            if (!::orderRepository.isInitialized)
                orderRepository = OderRepositoryImpl()
            return orderRepository
        }
    }
}