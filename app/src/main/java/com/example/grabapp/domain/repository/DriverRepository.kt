package com.example.grabapp.domain.repository

import com.example.grabapp.data.model.order.DriverResponse

interface DriverRepository {
    suspend fun getDriverInfo(id: String): DriverResponse?
}