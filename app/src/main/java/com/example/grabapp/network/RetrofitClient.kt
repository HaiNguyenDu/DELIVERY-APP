package com.example.grabapp.network

import com.example.grabapp.data.api.AuthApi
import com.example.grabapp.utils.SharedPreferencesUtils
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://quickdn.undo.it/api/"

    fun getRetrofitDefault(): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val okHttp = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun getRetrofitWithToken(
        sharedPreferencesUtils: SharedPreferencesUtils,
        apiAuth: AuthApi
    ): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val okHttp = OkHttpClient.Builder()
            .addInterceptor(
                AuthInterceptor(sharedPreferencesUtils)
            )
            .authenticator(
                TokenAuthenticator(sharedPreferencesUtils,apiAuth)
            )
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build()
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttp)
            .baseUrl(BASE_URL)
            .build()
    }

    fun <T> createDefaultApi(apiClass: Class<T>): T = getRetrofitDefault().create(apiClass)
    fun <T> createAuthApi(
        sharedPreferencesUtils: SharedPreferencesUtils,
        apiAuth: AuthApi, apiClass: Class<T>
    ): T =
        getRetrofitWithToken(sharedPreferencesUtils, apiAuth).create(apiClass)
}