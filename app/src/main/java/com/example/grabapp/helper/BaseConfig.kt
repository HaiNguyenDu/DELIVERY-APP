package com.example.grabapp.helper

import android.content.Context
import com.example.grabapp.common.GRAP_KEY

open class BaseConfig(private val context: Context) {
    val prefs = context.getSharedPreferences(GRAP_KEY, Context.MODE_PRIVATE)

    companion object {
        fun getInstance(context: Context) = BaseConfig(context)
    }

    inline fun <reified T> get(key: String, anonymousClass: Class<T>): T? {
        if (prefs == null) {
            throw RuntimeException("Please int Shared Preferences first!")
        }
        when (anonymousClass) {
            String::class.java -> {
                return prefs.getString(key, "") as T
            }

            Int::class.java -> {
                return prefs.getInt(key, 0) as T
            }

            Boolean::class.java -> {
                return prefs.getBoolean(key, false) as T
            }

            Float::class.java -> {
                return prefs.getFloat(
                    key,
                    0f
                ) as T
            }

            Long::class.java -> {
                return prefs.getLong(key, 0) as T
            }

            else -> {
                return null
            }
        }
    }

    inline fun <reified T> put(key: String, value: T) {
        if (prefs == null) {
            throw RuntimeException("Please int Shared Preferences first!")
        }
        val editor = prefs.edit()
        when (value) {
            is String -> {
                editor.putString(key, value)
            }

            is Int -> {
                editor.putInt(key, value)
            }

            is Boolean -> {
                editor.putBoolean(key, value)
            }

            is Float -> {
                editor.putFloat(key, value)
            }

            is Long -> {
                editor.putLong(key, value)
            }
        }
        editor.apply()
    }
}