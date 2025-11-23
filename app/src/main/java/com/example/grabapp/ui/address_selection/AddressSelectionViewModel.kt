package com.example.grabapp.ui.address_selection

import android.Manifest
import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.viewModelScope
import com.example.grabapp.base.BaseViewModel
import com.example.grabapp.data.repository.AddressRepository
import com.example.grabapp.domain.model.location.Address
import com.example.grabapp.domain.enum.EditTextEnum
import com.example.grabapp.domain.model.order.PackageInfo
import com.example.grabapp.respone.Coordinates
import com.example.grabapp.respone.GoongDirectionApiResponse
import com.example.grabapp.respone.Prediction
import com.example.grabapp.ui.address_selection.adapter.AddressSelectionPageAdapter
import com.example.grabapp.ui.address_selection.adapter.AddressSelectionPageAdapter.Companion.FRAGMENT_DETAIL_ORDER
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddressSelectionViewModel(private val application: Application) : BaseViewModel(application) {
    private lateinit var repository: AddressRepository

    private val _pagePosition = MutableStateFlow<Int>(0)

    val pagePosition: StateFlow<Int> = _pagePosition

    private val _listAddress = MutableStateFlow<List<Prediction>>(emptyList())
    val listAddress: StateFlow<List<Prediction>> = _listAddress
    private var _lastFocusEdt: EditTextEnum = EditTextEnum.NOT_THING
    private val _lastCoordinates = MutableStateFlow(Coordinates(0.0, 0.0))
    val lastCoordinates: StateFlow<Coordinates> = _lastCoordinates
    private val _pickUpAddress = MutableStateFlow(Address())
    val pickUpAddress: StateFlow<Address> = _pickUpAddress

    private val _dropOffAddress = MutableStateFlow(Address())
    val dropOffAddress: StateFlow<Address> = _dropOffAddress
    val _directionResponse = MutableStateFlow<GoongDirectionApiResponse?>(null)
    val directionResponse: StateFlow<GoongDirectionApiResponse?> = _directionResponse

    private var _edtLastTextEnumClicked = EditTextEnum.NOT_THING
    private var _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri

    private val _packageInfo = MutableStateFlow<PackageInfo?>(null)
    val packageInfo: StateFlow<PackageInfo?> = _packageInfo

    init {
        repository = AddressRepository.getInstance(application)
    }

    fun setPackageInfo(packageInfo: PackageInfo) {
        _packageInfo.value = packageInfo
    }

    fun setImageUri(uri: Uri?, onSuccess: (() -> Unit) = {}) {
        _imageUri.value = uri
        onSuccess()
    }

    fun setPage(index: Int) {
        _pagePosition.value = index
    }

    fun setLastEdtTextClicked(enum: EditTextEnum) {
        _edtLastTextEnumClicked = enum

    }

    fun getLastEdtTextClicked(): EditTextEnum {
        return _edtLastTextEnumClicked
    }

    fun setAddress(prediction: Prediction, onSuccess: () -> Unit) {
        prediction.place_id?.let { id ->
            repository.getDetailAddressById(id, {
                when (_lastFocusEdt) {
                    EditTextEnum.DROP_OFF -> {
                        _dropOffAddress.value = it.result.toAddress()
                        if (_pickUpAddress.value.address.isNotEmpty() && pagePosition.value == AddressSelectionPageAdapter.FRAGMENT_MAIN) {
                            _pagePosition.value = FRAGMENT_DETAIL_ORDER
                            onSuccess()
                        }
                    }

                    EditTextEnum.PICK_UP -> {
                        _pickUpAddress.value = it.result.toAddress()
                        if (_dropOffAddress.value.address.isNotEmpty() && pagePosition.value == AddressSelectionPageAdapter.FRAGMENT_MAIN) {
                            _pagePosition.value = FRAGMENT_DETAIL_ORDER
                            onSuccess()
                        }
                    }

                    else -> {

                    }
                }

            }, {

            })
        }
    }

    fun setLastFocusEdt(editText: EditTextEnum) {
        this._lastFocusEdt = editText
    }

    fun getLastFocusEdt(): EditTextEnum? {
        return _lastFocusEdt
    }

    fun searchAddress(input: String) {
        viewModelScope.launch {
            repository.searchAddress(input, {
                _listAddress.value = it.predictions
            }, {

            })
        }
    }

    fun setListAddress(newList: List<Prediction>) {
        _listAddress.value = newList
    }

    fun getCurrentAddress() {
        val coordinates = lastCoordinates.value
        viewModelScope.launch {
            repository.getAddressByCoordinates(coordinates, { response ->
                val address = response.results.getOrNull(0)?.toAddress()
                address?.let {
                    viewModelScope.launch {
                        _pickUpAddress.emit(it)
                    }
                }
            }, {

            })
        }
    }

    fun getDirection() {
        repository.getDirectionData(
            dropOffAddress.value,
            pickUpAddress.value,
            onSuccess = { data ->
                Log.e("checkOrder", "Route count: ${data.routes?.size}")

                _directionResponse.value = data
            },
            onError = { error ->
                Log.e("checkOrder", "Error: $error")
            }
        )
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getCurrentLocation() {
        viewModelScope.launch {
            repository.getCurrentLocation()?.let { location ->
                _lastCoordinates.value = Coordinates(location.latitude, location.longitude)
                getCurrentAddress()
            }
        }
    }
}