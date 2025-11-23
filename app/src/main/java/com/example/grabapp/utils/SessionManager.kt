package com.example.grabapp.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

object SessionManager {
    private val _logoutEvent = MutableSharedFlow<Unit>(replay = 0)
    val logoutEvent: SharedFlow<Unit> = _logoutEvent

    suspend fun triggerLogout() {
        _logoutEvent.emit(Unit)
    }
}