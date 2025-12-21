package com.example.grabapp.util

import android.util.Base64
import com.example.grabapp.data.model.JwtPayload
import com.google.gson.Gson

object JwtDecoder {
    private val gson = Gson()

    fun decodePayload(token: String): JwtPayload? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) {
                return null
            }
            val payload = parts[1]

            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP)
            val decodedString = String(decodedBytes, Charsets.UTF_8)

            gson.fromJson(decodedString, JwtPayload::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun extractUserId(token: String): String? {
        return decodePayload(token)?.sub
    }

    fun isTokenExpired(token: String): Boolean {
        val payload = decodePayload(token) ?: return true
        val exp = payload.exp ?: return true
        val currentTime = System.currentTimeMillis() / 1000
        return exp <= currentTime
    }
}
