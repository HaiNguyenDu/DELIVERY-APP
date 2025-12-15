package com.example.grabapp.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class OrderStorage(context: Context) {

    companion object {
        private const val KEY_ACTIVE_ORDER_ID = "active_order_id"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveActiveOrderId(orderId: String?) {
        prefs.edit {
            if (orderId != null) {
                putString(KEY_ACTIVE_ORDER_ID, orderId)
            } else {
                remove(KEY_ACTIVE_ORDER_ID)
            }
        }
    }

    fun getActiveOrderId(): String? {
        return prefs.getString(KEY_ACTIVE_ORDER_ID, null)
    }

    fun hasActiveOrder(): Boolean {
        return !getActiveOrderId().isNullOrEmpty()
    }

    fun clearActiveOrder() {
        prefs.edit {
            remove(KEY_ACTIVE_ORDER_ID)
        }
    }
}
