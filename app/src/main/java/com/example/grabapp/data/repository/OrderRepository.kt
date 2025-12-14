package com.example.grabapp.data.repository

import android.content.Context
import com.example.grabapp.api.AuthInterceptor
import com.example.grabapp.api.SelectiveLoggingInterceptor
import com.example.grabapp.common.BASE_URL
import com.example.grabapp.data.TokenStorage
import com.example.grabapp.data.model.AssignShipperRequest
import com.example.grabapp.data.model.OrderListResponse
import com.example.grabapp.data.model.OrderResponse
import com.example.grabapp.data.model.PackageInfo
import com.example.grabapp.data.model.UpdateOrderStatusRequest
import com.example.grabapp.data.model.UpdatePackageStatusRequest
import com.example.grabapp.data.order.OrderApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit

class OrderRepository(private val context: Context) {

    private val tokenStorage = TokenStorage(context)
    
    private val api: OrderApi by lazy {
        val bodyLogger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val headersLogger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }
        val selectiveLogging = SelectiveLoggingInterceptor(bodyLogger, headersLogger)
        val authInterceptor = AuthInterceptor(tokenStorage)
        
        val client = OkHttpClient.Builder()
            .addInterceptor(selectiveLogging)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
            
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OrderApi::class.java)
    }

    sealed class OrderListResult {
        data class Success(val response: OrderListResponse) : OrderListResult()
        data class Error(val code: Int?, val message: String) : OrderListResult()
    }
    
    sealed class OrderResult {
        data class Success(val response: OrderResponse) : OrderResult()
        data class Error(val code: Int?, val message: String) : OrderResult()
    }
    
    sealed class PackageResult {
        data class Success(val response: PackageInfo) : PackageResult()
        data class Error(val code: Int?, val message: String) : PackageResult()
    }

    suspend fun getOrders(role: String, userId: String): OrderListResult {
        return try {
            val resp = api.getOrders(role, userId)
            if (resp.isSuccessful) {
                val body = resp.body()
                if (body != null) {
                    OrderListResult.Success(body)
                } else {
                    OrderListResult.Error(resp.code(), "Empty response from server")
                }
            } else {
                val errorMes = try {
                    resp.errorBody()?.string()
                } catch (e: Exception) {
                    null
                }
                OrderListResult.Error(resp.code(), errorMes ?: "HTTP ${resp.code()}")
            }
        } catch (e: IOException) {
            OrderListResult.Error(
                null,
                "Network error: ${e.localizedMessage ?: "Please check your connection"}"
            )
        } catch (e: HttpException) {
            OrderListResult.Error(e.code(), e.message ?: "Server error")
        } catch (e: Exception) {
            OrderListResult.Error(null, e.message ?: "Unexpected error")
        }
    }
    
    suspend fun getOrderById(orderId: String): OrderResult {
        return try {
            val resp = api.getOrderById(orderId)
            if (resp.isSuccessful) {
                val body = resp.body()
                if (body != null) {
                    OrderResult.Success(body)
                } else {
                    OrderResult.Error(resp.code(), "Empty response from server")
                }
            } else {
                val errorMes = try {
                    resp.errorBody()?.string()
                } catch (e: Exception) {
                    null
                }
                OrderResult.Error(resp.code(), errorMes ?: "HTTP ${resp.code()}")
            }
        } catch (e: IOException) {
            OrderResult.Error(
                null,
                "Network error: ${e.localizedMessage ?: "Please check your connection"}"
            )
        } catch (e: HttpException) {
            OrderResult.Error(e.code(), e.message ?: "Server error")
        } catch (e: Exception) {
            OrderResult.Error(null, e.message ?: "Unexpected error")
        }
    }
    
    suspend fun assignShipper(orderId: String): OrderResult {
        return try {
            val request = AssignShipperRequest(orderId)
            val resp = api.assignShipper(request)
            if (resp.isSuccessful) {
                val body = resp.body()
                if (body != null) {
                    OrderResult.Success(body)
                } else {
                    OrderResult.Error(resp.code(), "Empty response from server")
                }
            } else {
                val errorMes = try {
                    resp.errorBody()?.string()
                } catch (e: Exception) {
                    null
                }
                OrderResult.Error(resp.code(), errorMes ?: "HTTP ${resp.code()}")
            }
        } catch (e: IOException) {
            OrderResult.Error(
                null,
                "Network error: ${e.localizedMessage ?: "Please check your connection"}"
            )
        } catch (e: HttpException) {
            OrderResult.Error(e.code(), e.message ?: "Server error")
        } catch (e: Exception) {
            OrderResult.Error(null, e.message ?: "Unexpected error")
        }
    }
    
    suspend fun updateOrderStatus(orderId: String, newStatus: String, reasonNote: String? = null): OrderResult {
        return try {
            val request = UpdateOrderStatusRequest(newStatus, reasonNote)
            val resp = api.updateOrderStatus(orderId, request)
            if (resp.isSuccessful) {
                val body = resp.body()
                if (body != null) {
                    OrderResult.Success(body)
                } else {
                    OrderResult.Error(resp.code(), "Empty response from server")
                }
            } else {
                val errorMes = try {
                    resp.errorBody()?.string()
                } catch (e: Exception) {
                    null
                }
                OrderResult.Error(resp.code(), errorMes ?: "HTTP ${resp.code()}")
            }
        } catch (e: IOException) {
            OrderResult.Error(
                null,
                "Network error: ${e.localizedMessage ?: "Please check your connection"}"
            )
        } catch (e: HttpException) {
            OrderResult.Error(e.code(), e.message ?: "Server error")
        } catch (e: Exception) {
            OrderResult.Error(null, e.message ?: "Unexpected error")
        }
    }
    
    suspend fun updatePackageStatus(packageId: String, status: String, note: String? = null): PackageResult {
        return try {
            val request = UpdatePackageStatusRequest(status, note)
            val resp = api.updatePackageStatus(packageId, request)
            if (resp.isSuccessful) {
                val body = resp.body()
                if (body != null) {
                    PackageResult.Success(body)
                } else {
                    PackageResult.Error(resp.code(), "Empty response from server")
                }
            } else {
                val errorMes = try {
                    resp.errorBody()?.string()
                } catch (e: Exception) {
                    null
                }
                PackageResult.Error(resp.code(), errorMes ?: "HTTP ${resp.code()}")
            }
        } catch (e: IOException) {
            PackageResult.Error(
                null,
                "Network error: ${e.localizedMessage ?: "Please check your connection"}"
            )
        } catch (e: HttpException) {
            PackageResult.Error(e.code(), e.message ?: "Server error")
        } catch (e: Exception) {
            PackageResult.Error(null, e.message ?: "Unexpected error")
        }
    }
}
