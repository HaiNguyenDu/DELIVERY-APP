package com.example.grabapp.driver.confirm_driver

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.grabapp.base.BaseViewModel
import com.example.grabapp.driver.confirm_driver.pager.ConfirmPager
import com.example.grabapp.driver.confirm_driver.pager.ConfirmationStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConfirmDriverViewModel(
    application: Application
) : BaseViewModel(application) {

    private val _pagerStates = MutableStateFlow(
        ConfirmPager.entries.associateWith { ConfirmationStatus.NONE }
    )
    val pagerStates = _pagerStates.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<ConfirmNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun updateConfirmationStatus(pager: ConfirmPager, status: ConfirmationStatus) {
        _pagerStates.value = _pagerStates.value.toMutableMap().apply {
            put(pager, status)
        }
    }

    fun areAllPagesConfirmed(): Boolean {
        return _pagerStates.value.values.all { it == ConfirmationStatus.YES }
    }

    fun navigateNextPage() {
        viewModelScope.launch {
            _navigationEvent.emit(ConfirmNavigationEvent.NEXT_PAGE)
        }
    }

    fun navigateRegister() {
        viewModelScope.launch {
            _navigationEvent.emit(ConfirmNavigationEvent.GO_REGISTER)
        }
    }

    fun navigateEligibility() {
        viewModelScope.launch {
            _navigationEvent.emit(ConfirmNavigationEvent.GO_ELIGIBILITY)
        }
    }
}

enum class ConfirmNavigationEvent {
    NEXT_PAGE, GO_REGISTER, GO_ELIGIBILITY
}
