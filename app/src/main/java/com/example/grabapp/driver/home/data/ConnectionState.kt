package com.example.grabapp.driver.home.data

import android.view.View
import com.example.grabapp.R

enum class ConnectionState(
    val backgroundRes: Int,
    val findingVisibility: Int,
    val trickVisibility: Int,
    val findingOrdersVisibility: Int
) {
    CONNECTED(
        backgroundRes = R.drawable.bg_gradient_green_connected,
        findingVisibility = View.VISIBLE,
        trickVisibility = View.GONE,
        findingOrdersVisibility = View.VISIBLE
    ),
    DISCONNECTED(
        backgroundRes = R.drawable.bg_gradient_green_disconnected,
        findingVisibility = View.GONE,
        trickVisibility = View.VISIBLE,
        findingOrdersVisibility = View.GONE
    );

    fun toggle(): ConnectionState = when (this) {
        CONNECTED -> DISCONNECTED
        DISCONNECTED -> CONNECTED
    }
}