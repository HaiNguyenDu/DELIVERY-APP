package com.example.grabapp.network

import com.example.grabapp.data.api.AuthApi
import com.example.grabapp.data.model.auth.RefreshTokenRequest
import com.example.grabapp.utils.SharedPreferencesUtils
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sharedPreferencesUtils: SharedPreferencesUtils,
    private val authApi: AuthApi
) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val token = sharedPreferencesUtils.getToken()
        request = if (token.isNotEmpty())
            request.newBuilder().addHeader("Authorization", "Bearer $token").build()
        else
            request
        var response = chain.proceed(request)
        if (response.code == 401) {
            response.close()
            val refreshToken = sharedPreferencesUtils.getRefreshToken()
            if (refreshToken.isNotEmpty()) {
                try {
                    val refreshResponse = runBlocking {
                        authApi.refreshToken(RefreshTokenRequest(refreshToken))
                    }
                    if (response.isSuccessful) {
                        val newToken = refreshResponse.body()?.accessToken!!
                        val newRefreshToken = refreshResponse.body()?.refreshToken!!
                        sharedPreferencesUtils.putToken(newToken)
                        sharedPreferencesUtils.putRefreshToken(newRefreshToken)
                        val newRequest =
                            request.newBuilder().addHeader("Authorization", "Bearer $newToken")
                                .build()
                        response = chain.proceed(newRequest)

                    } else {
                        sharedPreferencesUtils.putToken("")
                        sharedPreferencesUtils.putRefreshToken("")
                        throw Exception("Session expired, please login again")
                    }
                } catch (e: Exception) {
                    sharedPreferencesUtils.putToken("")
                    sharedPreferencesUtils.putRefreshToken("")
                    throw Exception(e)
                }
            } else {
                sharedPreferencesUtils.putToken("")
                sharedPreferencesUtils.putRefreshToken("")
                throw Exception("Session expired, please login again")
            }
        }
        return response
    }
}