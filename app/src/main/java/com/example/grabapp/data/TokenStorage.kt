package com.example.grabapp.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class TokenStorage(context: Context) {

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_TYPE = "token_type"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_PHONE = "phone"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveAuthTokens(accessToken: String, refreshToken: String?, tokenType: String?) {
        prefs.edit {
            putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .putString(KEY_TOKEN_TYPE, tokenType)
        }
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)
    fun getTokenType(): String? = prefs.getString(KEY_TOKEN_TYPE, null)
    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)

    fun saveUserId(userId: String) {
        prefs.edit {
            putString(KEY_USER_ID, userId)
        }
    }

    fun savePhone(phone: String) {
        prefs.edit {
            putString(KEY_PHONE, phone)
        }
    }

    fun getPhone(): String? = prefs.getString(KEY_PHONE, null)

    fun hasToken(): Boolean = !getAccessToken().isNullOrEmpty()

    fun clear() {
        prefs.edit { clear() }
    }
}