package com.example.grabapp.network

import com.example.grabapp.data.api.AuthApi
import com.example.grabapp.utils.SharedPreferencesUtils

class ApiProvider {
    companion object {
        private lateinit var apiProvider: ApiProvider
        fun getInstance(): ApiProvider {
            return apiProvider
        }

        fun init(sharedPreferencesUtils: SharedPreferencesUtils) {
            if (apiProvider == null) apiProvider = ApiProvider().init(sharedPreferencesUtils)
        }
    }

    private lateinit var sharedPreferencesUtils: SharedPreferencesUtils
    private lateinit var authApi: AuthApi

    fun init(sharedPreferencesUtils: SharedPreferencesUtils): ApiProvider {
        this.sharedPreferencesUtils = sharedPreferencesUtils
        authApi = RetrofitClient.createDefaultApi(AuthApi::class.java)
        return this
    }

    fun getAuthApi(): AuthApi = authApi

    fun <T> getAuthRequiredApi(apiClass: Class<T>): T {
        return RetrofitClient.createAuthApi(sharedPreferencesUtils, authApi, apiClass)
    }

    fun <T> getNoAuthApi(apiClass: Class<T>): T {
        return RetrofitClient.createDefaultApi(apiClass)
    }
}
