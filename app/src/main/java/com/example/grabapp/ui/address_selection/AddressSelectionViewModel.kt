package com.example.grabapp.ui.address_selection

import android.Manifest
import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.viewModelScope
import com.example.grabapp.base.BaseViewModel
import com.example.grabapp.data.repository.AddressRepository
import com.example.grabapp.domain.enum.EditTextEnum
import com.example.grabapp.domain.model.order.OrderForm
import com.example.grabapp.domain.model.order.PackageItemModel
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
    private val _orderForm = MutableStateFlow(OrderForm())
    val orderForm: StateFlow<OrderForm> = _orderForm
    val _directionResponse = MutableStateFlow<GoongDirectionApiResponse?>(null)
    val directionResponse: StateFlow<GoongDirectionApiResponse?> = _directionResponse

    private var _edtLastTextEnumClicked = EditTextEnum.NOT_THING
    private var _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri

    var selectPackagePosition = 0

    init {
        repository = AddressRepository.getInstance(application)
    }

    fun updatePackageInfo(packageInfo: PackageItemModel) {
        val updatedList = _orderForm.value.listPackageInfo.toMutableList()
        if (selectPackagePosition in updatedList.indices) {
            updatedList[selectPackagePosition] = packageInfo
            _orderForm.value = _orderForm.value.copy(listPackageInfo = updatedList)
        }
    }

    fun isHashInfoPackage(): Boolean {
        val listPackage = _orderForm.value.listPackageInfo
        listPackage.forEach {
            if (it.weightKg == 0.0 || !it.isHashInfo()) {
                return false
            }
        }
        return true
    }

    fun addPackageInfo(packageInfo: PackageItemModel) {
        val updatedList = _orderForm.value.listPackageInfo.toMutableList()
        updatedList.add(packageInfo)
        selectPackagePosition = updatedList.size - 1
        _orderForm.value = _orderForm.value.copy(listPackageInfo = updatedList)
    }

    fun deletePackageInfo(position: Int) {
        val updatedList = _orderForm.value.listPackageInfo.toMutableList()
        if (position in updatedList.indices) {
            updatedList.removeAt(position)
            _orderForm.value = _orderForm.value.copy(listPackageInfo = updatedList)
        }
    }

    fun getPackageInfo(position: Int): PackageItemModel {
        return _orderForm.value.listPackageInfo[position]
    }

    fun clearOrderForm() {
        _orderForm.value = OrderForm(
            pickupAddress = _orderForm.value.pickupAddress,
            listPackageInfo = listOf(PackageItemModel())
        )
        selectPackagePosition = 0
    }

    fun getCurrentPackageInfo(): PackageItemModel {
        return _orderForm.value.listPackageInfo[selectPackagePosition]
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

    fun setAddress(prediction: Prediction, position: Int = 0, onSuccess: () -> Unit) {
        prediction.place_id?.let { id ->
            repository.getDetailAddressById(id, { detailResponse ->
                when (_lastFocusEdt) {
                    EditTextEnum.DROP_OFF -> {
                        val currentOrder = _orderForm.value
                        if (currentOrder.listPackageInfo.isNotEmpty()) {
                            val updatedPackageList = currentOrder.listPackageInfo.toMutableList()
                            val firstPackage = updatedPackageList[position].copy(
                                dropOffAddress = detailResponse.result.toAddressInfo()
                            )
                            updatedPackageList[position] = firstPackage
                            _orderForm.value =
                                currentOrder.copy(listPackageInfo = updatedPackageList)
                        }
                        if (currentOrder.pickupAddress?.detail?.isNotEmpty() == true
                            && pagePosition.value == AddressSelectionPageAdapter.FRAGMENT_MAIN
                        ) {
                            _pagePosition.value = FRAGMENT_DETAIL_ORDER
                            onSuccess()
                        }
                    }

                    EditTextEnum.PICK_UP -> {
                        val currentOrder = _orderForm.value
                        _orderForm.value = currentOrder.copy(
                            pickupAddress = detailResponse.result.toAddressInfo()
                        )

                        if (currentOrder.listPackageInfo.firstOrNull()?.dropOffAddress?.detail?.isNotEmpty() == true
                            && pagePosition.value == AddressSelectionPageAdapter.FRAGMENT_MAIN
                        ) {
                            _pagePosition.value = AddressSelectionPageAdapter.FRAGMENT_DETAIL_ORDER
                            onSuccess()
                        }
                    }

                    else -> {}
                }
            }, {
                // Handle error
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
                val address = response.results.getOrNull(0)?.toAddressInfo()
                address?.let {
                    viewModelScope.launch {
                        _orderForm.value = _orderForm.value.copy(
                            pickupAddress = it
                        )
                    }
                }
            }, {

            })
        }
    }

    fun getDirection() {
        if (_orderForm.value.listPackageInfo.isEmpty()) return
        repository.getDirectionData(
            _orderForm.value.listPackageInfo[0].dropOffAddress,
            _orderForm.value.pickupAddress,
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