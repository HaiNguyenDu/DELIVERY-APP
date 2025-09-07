package com.example.grabapp.helper

import android.content.Context
import com.example.grabapp.common.GRAP_KEY

open class BaseConfig(private val context: Context) {
    private val prefs = context.getSharedPreferences(GRAP_KEY, Context.MODE_PRIVATE)

    companion object {
        fun getInstance(context: Context) = BaseConfig(context)
        //
    }
}