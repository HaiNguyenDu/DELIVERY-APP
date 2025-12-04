package com.example.grabapp.util

import android.content.Context
import androidx.core.content.edit
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

object FCMTokenHelper {
    private const val PREFS_NAME = "fcm_prefs"
    private const val KEY_FCM_TOKEN = "fcm_token"

    suspend fun getFCMToken(context: Context): String {
        return try {
            val firebaseMessaging = FirebaseMessaging.getInstance()
            val token = firebaseMessaging.token.await()
            
            if (token.isNullOrBlank()) {
                getCachedToken(context) ?: throw Exception("Không thể lấy FCM token")
            } else {
                saveFCMToken(context, token)
                token
            }
        } catch (e: Exception) {
            e.printStackTrace()
            getCachedToken(context) ?: throw Exception("Không thể lấy FCM token: ${e.message}")
        }
    }

    private fun getCachedToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_FCM_TOKEN, null)
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

