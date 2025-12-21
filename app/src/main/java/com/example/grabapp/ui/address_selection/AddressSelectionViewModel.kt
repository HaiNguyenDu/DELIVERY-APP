package com.example.grabapp.ui.address_selection

import OrderStatus
import android.Manifest
import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.example.grabapp.base.BaseViewModel
import com.example.grabapp.data.model.order.AddressInfo
import com.example.grabapp.data.model.order.CreateOrderResponse
import com.example.grabapp.data.model.order.PriceRouteItem
import com.example.grabapp.data.model.payment.PayOSPaymentRequest
import com.example.grabapp.data.model.payment.PaymentStatusEnum
import com.example.grabapp.data.repository.AddressRepository
import com.example.grabapp.data.repository.FileRepositoryImpl
import com.example.grabapp.data.repository.OderRepositoryImpl
import com.example.grabapp.data.repository.PaymentRepositoryImpl
import com.example.grabapp.data.repository.UserRepositoryImpl
import com.example.grabapp.domain.enum.EditTextEnum
import com.example.grabapp.domain.enum.PaymentTypeEnum
import com.example.grabapp.domain.model.order.OrderForm
import com.example.grabapp.domain.model.order.PackageItemModel
import com.example.grabapp.domain.model.user.User
import com.example.grabapp.domain.model.user.toUser
import com.example.grabapp.domain.repository.OrderRepository
import com.example.grabapp.domain.repository.PaymentRepository
import com.example.grabapp.respone.Coordinates
import com.example.grabapp.respone.GoongDirectionApiResponse
import com.example.grabapp.respone.Prediction
import com.example.grabapp.ui.address_selection.adapter.AddressSelectionPageAdapter.Companion.FRAGMENT_DETAIL_ORDER
import com.example.grabapp.utils.JwtUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AddressSelectionViewModel(private val application: Application) : BaseViewModel(application) {
    private var addressRepository: AddressRepository = AddressRepository.getInstance(application)
    private var orderRepository: OrderRepository = OderRepositoryImpl.getInstance()
    val userRepository = UserRepositoryImpl()
    private var paymentRepository: PaymentRepository = PaymentRepositoryImpl.getInstance()
    private val _pagePosition = MutableStateFlow<Int>(0)

    val pagePosition: StateFlow<Int> = _pagePosition

    private val _listAddress = MutableStateFlow<List<Prediction>>(emptyList())
    val listAddress: StateFlow<List<Prediction>> = _listAddress
    private var _lastFocusEdt: EditTextEnum = EditTextEnum.NOT_THING
    private val _lastCoordinates = MutableStateFlow(Coordinates(0.0, 0.0))
    val lastCoordinates: StateFlow<Coordinates> = _lastCoordinates
    private val _orderForm = MutableStateFlow(OrderForm())
    val orderForm: StateFlow<OrderForm> = _orderForm
    val _directionResponses = MutableStateFlow<List<GoongDirectionApiResponse>?>(null)
    val directionResponses: StateFlow<List<GoongDirectionApiResponse>?> = _directionResponses

    private var _edtLastTextEnumClicked = EditTextEnum.NOT_THING
    private var _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri

    var selectPackagePosition = 0

    private val _isCreateSuccess = MutableSharedFlow<Boolean>()
    val isCreateSuccess: SharedFlow<Boolean> = _isCreateSuccess

    val fileRepository = FileRepositoryImpl()
    private val _isShowQrCode = MutableSharedFlow<Boolean>()
    val isShowQrCode: SharedFlow<Boolean> = _isCreateSuccess
    private var _lastAddress: AddressInfo? = null
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user
    private val _paymentResponse = MutableStateFlow<CreateOrderResponse?>(null)
    val paymentResponse: StateFlow<CreateOrderResponse?> = _paymentResponse

    suspend fun isPaid(): Boolean = withContext(Dispatchers.IO) {
        paymentResponse.value?.orderId?.let { orderId ->
            val data = orderRepository.getOrderDetail(orderId)
            data?.paymentStatus == PaymentStatusEnum.PAID
        } ?: false
    }

    fun updateStatus(){
        viewModelScope.launch {
            paymentResponse.value?.orderId?.let {
                orderRepository.updateStatus(it, OrderStatus.CANCELLED_BY_SENDER)
                Toast.makeText(application, "Bạn đã hủy đơn hàng thành công", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun setIsSuccess(){
        viewModelScope.launch {
            _isCreateSuccess.emit(true)
        }
    }

    fun loadUser() {
        viewModelScope.launch {
            val userResponse =
                userRepository.getUserByIdFromSever(JwtUtils.getUserId(application) ?: "")
            userResponse?.let {
                _user.value = it.toUser()
                _orderForm.value = _orderForm.value.copy(
                    pickupAddress = _orderForm.value.pickupAddress.copy(
                        name = it.fullName,
                        phone = it.phone,
                    )
                )
            }
        }
    }

    fun setPaymentType(type: PaymentTypeEnum) {
        _orderForm.value = _orderForm.value.copy(
            paymentTypeEnum = type
        )
    }

    fun updatePickUpAddress(addressInfo: AddressInfo) {
        _orderForm.value = _orderForm.value.copy(pickupAddress = addressInfo)
    }

    fun setLastAddress(address: AddressInfo?) {
        _lastAddress = address
    }

    suspend fun getPriceAndRoute(): String {
        return withContext(Dispatchers.IO) {
            val listData = orderRepository.getPriceAndRoute(_orderForm.value)
            var cost = 0.0
            listData.forEach {
                cost += it.price
            }
            cost.toString()
        }
    }

    fun onBackPressLocationInfo() {
        if (_lastAddress == null) return
        when (_lastFocusEdt) {
            EditTextEnum.DROP_OFF -> {
                updatePackageInfo(getCurrentPackageInfo().copy(dropOffAddress = _lastAddress!!))
            }

            EditTextEnum.PICK_UP -> {
                _orderForm.value = _orderForm.value.copy(pickupAddress = _lastAddress!!)
            }

            else -> {}
        }
        _lastAddress = null
        _lastFocusEdt = EditTextEnum.NOT_THING
    }

    fun createOrder(onSuccess: (String) -> Unit, onFail: () -> Unit) {
        showLoading()
        viewModelScope.launch {
            val listPackage = _orderForm.value.listPackageInfo
            val newList = listPackage.map {
                val file = uriToFile(application, it.imgUrl.toUri(), "avatar.jpg")
                val url = fileRepository.upload(_user.value?.id ?: "", file)
                it.copy(
                    imgUrl = url?.url.toString()
                )
            }
            val newOrder = _orderForm.value.copy(
                listPackageInfo = newList
            )
            val result = orderRepository.createOrder(newOrder)
            result.onSuccess {
                onSuccess(it.orderId)
                _paymentResponse.value = it
                hideLoading()
                if (PaymentTypeEnum.ONLINE == _orderForm.value.paymentTypeEnum)
                    return@onSuccess
                _isCreateSuccess.emit(true)
            }.onFailure {
                onFail()
                hideLoading()
            }
        }
    }

    fun uriToFile(context: Context, uri: Uri, fileName: String): File {
        val inputStream = context.contentResolver.openInputStream(uri)!!
        val file = File(context.cacheDir, fileName)
        file.createNewFile()
        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }
        return file
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
            if (!it.isHashInfo()) {
                return false
            }
        }
        return true
    }


    fun canCalculatePrice(): Boolean {
        val listPackage = _orderForm.value.listPackageInfo
        listPackage.forEach {
            if (!it.canCalculatePrice()) {
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
        _lastFocusEdt = EditTextEnum.NOT_THING
        _edtLastTextEnumClicked = EditTextEnum.NOT_THING
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
            addressRepository.getDetailAddressById(id, { detailResponse ->
                Log.d("test", _lastFocusEdt.name)
                when (_lastFocusEdt) {
                    EditTextEnum.DROP_OFF -> {
                        Log.d("test", "drop ooff")
                        val currentOrder = _orderForm.value
                        if (currentOrder.listPackageInfo.isNotEmpty()) {
                            val updatedPackageList = currentOrder.listPackageInfo.toMutableList()
                            val firstPackage = updatedPackageList[selectPackagePosition].copy(
                                dropOffAddress = detailResponse.result.toAddressInfo()
                            )
                            updatedPackageList[selectPackagePosition] = firstPackage
                            _orderForm.value =
                                currentOrder.copy(listPackageInfo = updatedPackageList)
                        }
                        if (currentOrder.pickupAddress.detail.isNotEmpty()) {
                            _pagePosition.value = FRAGMENT_DETAIL_ORDER
                            onSuccess()
                        }
                    }

                    EditTextEnum.PICK_UP -> {
                        val currentOrder = _orderForm.value
                        _orderForm.value = currentOrder.copy(
                            pickupAddress = _orderForm.value.pickupAddress.copy(
                                detail = detailResponse.result.formatted_address,
                            )
                        )

                        if (currentOrder.listPackageInfo.firstOrNull()?.dropOffAddress?.detail?.isNotEmpty() == true) {
                            _pagePosition.value = FRAGMENT_DETAIL_ORDER
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
            addressRepository.searchAddress(input, {
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
            addressRepository.getAddressByCoordinates(coordinates, { response ->
                val address = response.results.getOrNull(0)?.toAddressInfo()
                address?.let {
                    viewModelScope.launch {
                        _orderForm.value = _orderForm.value.copy(
                            pickupAddress = AddressInfo(
                                name = _orderForm.value.pickupAddress.name,
                                phone = orderForm.value.pickupAddress.phone,
                                detail = it.detail,
                                districtCode = it.districtCode,
                                latitude = it.latitude,
                                longitude = it.longitude,
                                wardCode = it.wardCode
                            )
                        )
                    }
                }
            }, {

            })
        }
    }

    fun getDirection() {
        viewModelScope.launch {
            val listData = orderRepository.getPriceAndRoute(orderForm.value)
            val listResponse =
                addressRepository.getDirectionData(
                    _orderForm.value.pickupAddress,
                    listData
                )
            _directionResponses.value = listResponse
        }
    }

    suspend fun getListRoute(): List<PriceRouteItem> = withContext(Dispatchers.IO) {
        orderRepository.getPriceAndRoute(orderForm.value)
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getCurrentLocation() {
        viewModelScope.launch {
            addressRepository.getCurrentLocation()?.let { location ->
                _lastCoordinates.value = Coordinates(location.latitude, location.longitude)
                getCurrentAddress()
            }
        }
    }

    fun createAndRedirectToPayment() {
        viewModelScope.launch {
            val paymentRequest = PayOSPaymentRequest(
                orderCode = System.currentTimeMillis(),
                amount = getPriceAndRoute().toLong(),
                description = "Thanh toán cho đơn hàng #1234",
                cancelUrl = "app://yourapp.com/payment/cancel",
                returnUrl = "app://yourapp.com/payment/success",
            )
            val response = paymentRepository.createPayment(paymentRequest)
            response.onSuccess {

            }
        }
    }
}