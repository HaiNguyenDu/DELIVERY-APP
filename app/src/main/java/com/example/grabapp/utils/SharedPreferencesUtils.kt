package com.example.grabapp.utils

import android.content.Context
import com.example.grabapp.helper.BaseConfig

class SharedPreferencesUtils(
    private val context: Context,
) {
    companion object {
        private const val KEY_TOKEN = "KEY_TOKEN"
        private const val KEY_REFRESH_TOKEN = "KEY_REFRESH_TOKEN"

        fun getInstance(context: Context): SharedPreferencesUtils {
            return SharedPreferencesUtils(context)
        }
    }

    fun putToken(token: String) {
        BaseConfig.getInstance(context).put(KEY_TOKEN, token)
    }

    fun getToken(): String {
        return BaseConfig.getInstance(context).get(KEY_TOKEN, String::class.java) ?: ""
    }

    fun putRefreshToken(token: String) {
        BaseConfig.getInstance(context).put(KEY_REFRESH_TOKEN, token)
    }

    fun getRefreshToken(): String {
        return BaseConfig.getInstance(context).get(KEY_REFRESH_TOKEN, String::class.java) ?: ""
    }
}