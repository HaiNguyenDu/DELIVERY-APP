package com.example.grabapp.driver.register

import android.app.Application
import com.example.grabapp.base.BaseViewModel
import com.example.grabapp.driver.register.data.IdentificationCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RegisterViewModel(
    application: Application
) : BaseViewModel(application) {
    
    val identificationCard = IdentificationCard()
    
    private val _isIdentificationCardValid = MutableStateFlow(false)
    val isIdentificationCardValid: StateFlow<Boolean> = _isIdentificationCardValid
    
    fun updateIdentificationCardValidation() {
        _isIdentificationCardValid.value = identificationCard.isFullyValid()
    }
}
