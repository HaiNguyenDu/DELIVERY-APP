package com.example.grabapp.data.repository

import android.util.Log
import com.example.grabapp.data.api.DriverApi
import com.example.grabapp.data.model.order.DriverResponse
import com.example.grabapp.domain.repository.DriverRepository
import com.example.grabapp.network.ApiProvider

class DriverRepositoryImpl : DriverRepository {
    val api = ApiProvider.getInstance().getAuthRequiredApi(DriverApi::class.java)

    override suspend fun getDriverInfo(id: String): DriverResponse? {
        try {
            val response = api.getDriverInfo(id)
            if (response.isSuccessful && response.body() != null) {
                return response.body()
            }
            return null
        } catch (e: Exception) {
            Log.d("dsd",e.message?:"")
            return null
        }
    }
}
