package com.example.grabapp.network

import com.example.grabapp.data.api.AuthApi
import com.example.grabapp.data.model.auth.RefreshTokenRequest
import com.example.grabapp.utils.SessionManager
import com.example.grabapp.utils.SharedPreferencesUtils
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sharedPreferencesUtils: SharedPreferencesUtils,
) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sharedPreferencesUtils.getToken()

        val request = if (token.isNotEmpty()) {
            chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else chain.request()

        return chain.proceed(request)
    }
}