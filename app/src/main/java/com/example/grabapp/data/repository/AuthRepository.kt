package com.example.grabapp.data.repository

import retrofit2.HttpException
import com.example.grabapp.data.auth.AuthApi
import com.example.grabapp.data.model.AuthRequest
import com.example.grabapp.data.model.AuthResponse
import com.example.grabapp.data.TokenStorage
import com.example.grabapp.util.JwtDecoder
import java.io.IOException

class AuthRepository(
    private val api: AuthApi,
    private val tokenStorage: TokenStorage
) {
    sealed class LoginResult {
        data class Success(val response: AuthResponse) : LoginResult()
        data class Error(val code: Int?, val message: String) : LoginResult()
    }

    suspend fun login(phone: String, password: String): LoginResult {
        return try {
            val resp = api.login(AuthRequest(phone, password))
            if (resp.isSuccessful) {
                val body = resp.body()
                if (body != null && !body.accessToken.isNullOrEmpty()) {
                    tokenStorage.saveAuthTokens(body.accessToken, body.refreshToken, body.tokenType)
                    
                    // Decode JWT token and save user ID (UUID)
                    val userId = JwtDecoder.extractUserId(body.accessToken)
                    userId?.let {
                        tokenStorage.saveUserId(it)
                    }
                    
                    LoginResult.Success(body)
                } else {
                    LoginResult.Error(resp.code(), "Empty response from server")
                }
            } else {
                val errorMes = try {
                    resp.errorBody()?.string()
                } catch (e: Exception) {
                    null
                }
                LoginResult.Error(resp.code(), errorMes ?: "HTTP ${resp.code()}")
            }
        } catch (e: IOException) {
            LoginResult.Error(
                null,
                "Network error: ${e.localizedMessage ?: "Please check your connection"}"
            )
        } catch (e: HttpException) {
            LoginResult.Error(e.code(), e.message ?: "Server error")
        } catch (e: Exception) {
            LoginResult.Error(null, e.message ?: "Unexpected error")
        }
    }
}
