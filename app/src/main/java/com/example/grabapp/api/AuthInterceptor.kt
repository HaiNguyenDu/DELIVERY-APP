package com.example.grabapp.api

import com.example.grabapp.data.TokenStorage
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor(
    private val tokenStorage: TokenStorage
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        val accessToken = tokenStorage.getAccessToken()
        val tokenType = tokenStorage.getTokenType() ?: "Bearer"
        
        val newRequest = if (accessToken != null) {
            originalRequest.newBuilder()
                .header("Authorization", "$tokenType $accessToken")
                .build()
        } else {
            originalRequest
        }
        
        return chain.proceed(newRequest)
    }
}
