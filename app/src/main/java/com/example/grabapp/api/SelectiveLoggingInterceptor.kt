package com.example.grabapp.api

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException

class SelectiveLoggingInterceptor(
    private val bodyLogger: HttpLoggingInterceptor,
    private val headersLogger: HttpLoggingInterceptor
) : Interceptor {

    companion object {
        private const val VERIFY_FACE_ENDPOINT = "/ai/face/verify"
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()

        return if (url.contains(VERIFY_FACE_ENDPOINT)) {
            headersLogger.intercept(chain)
        } else {
            bodyLogger.intercept(chain)
        }
    }
}
