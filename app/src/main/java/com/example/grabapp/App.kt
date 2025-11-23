package com.example.grabapp

import android.app.Application
import com.example.grabapp.network.ApiProvider
import com.example.grabapp.utils.SharedPreferencesUtils

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ApiProvider.init(sharedPreferencesUtils = SharedPreferencesUtils(this))
    }
}