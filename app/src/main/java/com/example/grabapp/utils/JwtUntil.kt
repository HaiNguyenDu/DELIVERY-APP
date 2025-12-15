package com.example.grabapp.utils

import android.content.Context
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import java.util.Date

object JwtUtils {
    fun decode(token: String): DecodedJWT? {
        return try {
            JWT.decode(token)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    fun getUserId(context: Context): String? {
        val token = SharedPreferencesUtils.getInstance(context).getToken()
        val jwt = decode(token)
        return jwt?.subject
    }

    fun getExpirationDate(token: String): Date? {
        val jwt = decode(token)
        return jwt?.expiresAt
    }

    fun isTokenExpired(token: String): Boolean {
        val expiresAt = getExpirationDate(token) ?: return true
        return expiresAt.before(Date())
    }

    fun getClaim(token: String, claimName: String): String? {
        val jwt = decode(token)
        return jwt?.getClaim(claimName)?.asString()
    }

    fun getLongClaim(token: String, claimName: String): Long? {
        val jwt = decode(token)
        return jwt?.getClaim(claimName)?.asLong()
    }
}