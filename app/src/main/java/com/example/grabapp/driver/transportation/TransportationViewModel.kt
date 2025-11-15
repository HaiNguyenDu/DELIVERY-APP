package com.example.grabapp.driver.transportation

import android.app.Application
import com.example.grabapp.base.BaseViewModel
import com.example.grabapp.model.Transportation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TransportationViewModel(
    application: Application
) : BaseViewModel(application) {

    private val _selectedTransportation = MutableStateFlow(Transportation.GRAB_BIKE)
    val selectedTransportation: StateFlow<Transportation> = _selectedTransportation

    fun setSelectedTransportation(transportation: Transportation) {
        _selectedTransportation.value = transportation
    }
}
