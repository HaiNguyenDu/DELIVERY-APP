package com.example.grabapp.network

import android.util.Log
import com.example.grabapp.data.api.AuthApi
import com.example.grabapp.data.model.auth.RefreshTokenRequest
import com.example.grabapp.utils.SessionManager
import com.example.grabapp.utils.SharedPreferencesUtils
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    val sharedPreferences: SharedPreferencesUtils,
    private val authApi: AuthApi
) :
    Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = sharedPreferences.getRefreshToken()

        if (refreshToken.isEmpty()) {
            sharedPreferences.clearSession()
            return null
        }

        if (responseCount(response) >= 2) {
            SessionManager.triggerLogout()
            return null
        }

        return try {
            val refreshResponse = authApi.refreshToken(RefreshTokenRequest(refreshToken)).execute()

            if (!refreshResponse.isSuccessful) {
                sharedPreferences.clearSession()
                return null
            }

            val newAccess = refreshResponse.body()?.accessToken ?: ""
            val newRefresh = refreshResponse.body()?.refreshToken ?: ""
            Log.d("delivery_app",newAccess)
            sharedPreferences.putToken(newAccess)
            sharedPreferences.putRefreshToken(newRefresh)
            response.request.newBuilder()
                .header("Authorization", "Bearer $newAccess")
                .build()
        } catch (e: Exception) {
            sharedPreferences.clearSession()
            null
        }

    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var r = response.priorResponse
        while (r != null) {
            count++
            r = r.priorResponse
        }
        return count
    }
}