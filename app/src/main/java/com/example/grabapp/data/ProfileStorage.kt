package com.example.grabapp.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.grabapp.data.model.DriverProfile

class ProfileStorage(context: Context) {

    companion object {
        private const val KEY_PROFILE = "driver_profile"
        private const val KEY_IS_PENDING = "is_pending"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveProfile(profile: DriverProfile) {
        prefs.edit {
            putString(KEY_PROFILE, DriverProfile.toJson(profile))
        }
    }

    fun getProfile(): DriverProfile? {
        val json = prefs.getString(KEY_PROFILE, null) ?: return null
        return DriverProfile.fromJson(json)
    }

    fun setPendingStatus(isPending: Boolean) {
        prefs.edit {
            putBoolean(KEY_IS_PENDING, isPending)
        }
    }

    fun isPending(): Boolean {
        return prefs.getBoolean(KEY_IS_PENDING, false)
    }

    fun clearProfile() {
        prefs.edit {
            remove(KEY_PROFILE)
            remove(KEY_IS_PENDING)
        }
    }
}
