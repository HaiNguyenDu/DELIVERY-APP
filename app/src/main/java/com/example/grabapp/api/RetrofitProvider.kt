package com.example.grabapp.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitProvider {
    fun create(baseUrl: String): Retrofit {
        val bodyLogger = HttpLoggingInterceptor().apply { 
            level = HttpLoggingInterceptor.Level.BODY 
        }
        val headersLogger = HttpLoggingInterceptor().apply { 
            level = HttpLoggingInterceptor.Level.HEADERS 
        }
        val selectiveLogging = SelectiveLoggingInterceptor(bodyLogger, headersLogger)
        val client = OkHttpClient.Builder()
            .addInterceptor(selectiveLogging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
