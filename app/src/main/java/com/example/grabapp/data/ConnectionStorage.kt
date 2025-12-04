package com.example.grabapp.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.grabapp.driver.home.data.ConnectionState

class ConnectionStorage(context: Context) {

    companion object {
        private const val KEY_CONNECTION_STATE = "connection_state"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveConnectionState(state: ConnectionState) {
        prefs.edit {
            putString(KEY_CONNECTION_STATE, state.name)
        }
    }

    fun getConnectionState(): ConnectionState {
        val stateName = prefs.getString(KEY_CONNECTION_STATE, null)
        return try {
            ConnectionState.valueOf(stateName ?: ConnectionState.DISCONNECTED.name)
        } catch (e: Exception) {
            ConnectionState.DISCONNECTED
        }
    }

    fun clearConnectionState() {
        prefs.edit {
            remove(KEY_CONNECTION_STATE)
        }
    }
}
