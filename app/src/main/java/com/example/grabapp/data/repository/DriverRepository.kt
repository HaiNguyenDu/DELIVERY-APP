package com.example.grabapp.data.repository

import android.content.Context
import com.example.grabapp.api.AuthInterceptor
import com.example.grabapp.api.SelectiveLoggingInterceptor
import com.example.grabapp.common.BASE_URL
import com.example.grabapp.data.TokenStorage
import com.example.grabapp.data.auth.DriverApi
import com.example.grabapp.data.model.DriverRegisterRequest
import com.example.grabapp.data.model.DriverRegisterResponse
import com.example.grabapp.data.model.UpdateDriverStatusRequest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit

class DriverRepository(private val context: Context) {

    private val tokenStorage = TokenStorage(context)
    
    private val api: DriverApi by lazy {
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
            .create(DriverApi::class.java)
    }

    sealed class RegisterResult {
        data class Success(val response: DriverRegisterResponse) : RegisterResult()
        data class Error(val code: Int?, val message: String) : RegisterResult()
    }

    sealed class DriverInfoResult {
        data class Success(val response: DriverRegisterResponse) : DriverInfoResult()
        object NotFound : DriverInfoResult()
        data class Error(val code: Int?, val message: String) : DriverInfoResult()
    }

    sealed class UpdateStatusResult {
        object Success : UpdateStatusResult()
        data class Error(val code: Int?, val message: String) : UpdateStatusResult()
    }

    suspend fun register(request: DriverRegisterRequest): RegisterResult {
        return try {
            val resp = api.register(request)
            if (resp.isSuccessful) {
                val body = resp.body()
                if (body != null) {
                    RegisterResult.Success(body)
                } else {
                    RegisterResult.Error(resp.code(), "Empty response from server")
                }
            } else {
                val errorMes = try {
                    resp.errorBody()?.string()
                } catch (e: Exception) {
                    null
                }
                RegisterResult.Error(resp.code(), errorMes ?: "HTTP ${resp.code()}")
            }
        } catch (e: IOException) {
            RegisterResult.Error(
                null,
                "Network error: ${e.localizedMessage ?: "Please check your connection"}"
            )
        } catch (e: HttpException) {
            RegisterResult.Error(e.code(), e.message ?: "Server error")
        } catch (e: Exception) {
            RegisterResult.Error(null, e.message ?: "Unexpected error")
        }
    }

    suspend fun getDriverInfo(userId: String): DriverInfoResult {
        return try {
            val resp = api.getDriver(userId)
            if (resp.isSuccessful) {
                val body = resp.body()
                if (body != null) {
                    DriverInfoResult.Success(body)
                } else {
                    DriverInfoResult.Error(resp.code(), "Empty response from server")
                }
            } else {
                if (resp.code() == 404) {
                    DriverInfoResult.NotFound
                } else {
                    val errorMes = try {
                        resp.errorBody()?.string()
                    } catch (e: Exception) {
                        null
                    }
                    DriverInfoResult.Error(resp.code(), errorMes ?: "HTTP ${resp.code()}")
                }
            }
        } catch (e: IOException) {
            DriverInfoResult.Error(
                null,
                "Network error: ${e.localizedMessage ?: "Please check your connection"}"
            )
        } catch (e: HttpException) {
            DriverInfoResult.Error(e.code(), e.message ?: "Server error")
        } catch (e: Exception) {
            DriverInfoResult.Error(null, e.message ?: "Unexpected error")
        }
    }

    suspend fun updateDriverStatus(request: UpdateDriverStatusRequest): UpdateStatusResult {
        return try {
            val resp = api.updateDriverStatus(request)
            if (resp.isSuccessful) {
                UpdateStatusResult.Success
            } else {
                val errorMes = try {
                    resp.errorBody()?.string()
                } catch (e: Exception) {
                    null
                }
                UpdateStatusResult.Error(resp.code(), errorMes ?: "HTTP ${resp.code()}")
            }
        } catch (e: IOException) {
            UpdateStatusResult.Error(
                null,
                "Network error: ${e.localizedMessage ?: "Please check your connection"}"
            )
        } catch (e: HttpException) {
            UpdateStatusResult.Error(e.code(), e.message ?: "Server error")
        } catch (e: Exception) {
            UpdateStatusResult.Error(null, e.message ?: "Unexpected error")
        }
    }
}
