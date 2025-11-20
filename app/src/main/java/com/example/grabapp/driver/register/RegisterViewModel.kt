package com.example.grabapp.driver.register

import android.app.Application
import com.example.grabapp.base.BaseViewModel
import com.example.grabapp.driver.register.data.DrivingLicenseData
import com.example.grabapp.driver.register.data.EmergencyContactData
import com.example.grabapp.driver.register.data.IdentificationCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RegisterViewModel(
    application: Application
) : BaseViewModel(application) {
    
    val identificationCard = IdentificationCard()
    val drivingLicense = DrivingLicenseData()
    val emergencyContact = EmergencyContactData()
    
    private val _isIdentificationCardValid = MutableStateFlow(false)
    val isIdentificationCardValid: StateFlow<Boolean> = _isIdentificationCardValid
    
    private val _isDrivingLicenseValid = MutableStateFlow(false)
    val isDrivingLicenseValid: StateFlow<Boolean> = _isDrivingLicenseValid
    
    private val _isEmergencyContactValid = MutableStateFlow(false)
    val isEmergencyContactValid: StateFlow<Boolean> = _isEmergencyContactValid
    
    fun updateIdentificationCardValidation() {
        _isIdentificationCardValid.value = identificationCard.isFullyValid()
    }
    
    fun updateDrivingLicenseValidation() {
        _isDrivingLicenseValid.value = drivingLicense.isFullyValid()
    }
    
    fun updateEmergencyContactValidation() {
        _isEmergencyContactValid.value = emergencyContact.isFullyValid()
    }
}
