package com.example.grabapp.util

import android.content.Context
import androidx.core.content.edit

object FCMTokenHelper {
    private const val PREFS_NAME = "fcm_prefs"
    private const val KEY_FCM_TOKEN = "fcm_token"

    fun getFCMToken(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedToken = prefs.getString(KEY_FCM_TOKEN, null)
        
        return if (savedToken.isNullOrBlank()) {
            val placeholderToken = "fcm_token_${System.currentTimeMillis()}"
            saveFCMToken(context, placeholderToken)
            placeholderToken
        } else {
            savedToken
        }
    }

    fun saveFCMToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putString(KEY_FCM_TOKEN, token)
        }
    }

    fun clearFCMToken(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            remove(KEY_FCM_TOKEN)
        }
    }
}

