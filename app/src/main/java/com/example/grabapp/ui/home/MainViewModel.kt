package com.example.grabapp.ui.home

import OrderStatus
import android.app.Application
import android.util.Log
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.grabapp.base.BaseViewModel
import com.example.grabapp.data.local.user.UserDao
import com.example.grabapp.data.model.order.AddressInfo
import com.example.grabapp.data.model.order.DriverLocationResponse
import com.example.grabapp.data.model.order.DriverResponse
import com.example.grabapp.data.model.order.OrderItem
import com.example.grabapp.data.model.order.OrderItemResponse
import com.example.grabapp.data.model.order.PriceRouteItem
import com.example.grabapp.data.repository.AddressRepository
import com.example.grabapp.data.repository.DriverRepositoryImpl
import com.example.grabapp.data.repository.OderRepositoryImpl
import com.example.grabapp.data.repository.UserRepositoryImpl
import com.example.grabapp.domain.model.order.toOrderForm
import com.example.grabapp.domain.model.user.User
import com.example.grabapp.domain.model.user.toUser
import com.example.grabapp.respone.GoongDirectionApiResponse
import com.example.grabapp.utils.JwtUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(userDao: UserDao, application: Application) : BaseViewModel(application) {

    private val repository = UserRepositoryImpl()
    private val driverRepository = DriverRepositoryImpl()
    private val orderRepository = OderRepositoryImpl()
    private val _listOrder = MutableStateFlow<List<OrderItem>>(emptyList())
    val listOrder: StateFlow<List<OrderItem>> = _listOrder
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _currentOrder = MutableStateFlow<OrderItemResponse?>(null)
    val currentOrder: StateFlow<OrderItemResponse?> = _currentOrder

    private val _selectedOrder = MutableSharedFlow<OrderItemResponse>()
    val selectedOrder: SharedFlow<OrderItemResponse> = _selectedOrder
    var isLoop = false
    val _directionResponses = MutableStateFlow<List<GoongDirectionApiResponse>?>(null)
    val directionResponses: StateFlow<List<GoongDirectionApiResponse>?> = _directionResponses
    val addressRepository = AddressRepository.getInstance(application)

    private val _driverInfo = MutableStateFlow<DriverResponse?>(null)
    val driverInfo: StateFlow<DriverResponse?> = _driverInfo
    var driverLocationResponse: DriverLocationResponse? = null
    init {
        loadUser()
        loadListOrder()
    }

    fun getDirection() {
        viewModelScope.launch {
            if (currentOrder.value == null) return@launch
            val listData = orderRepository.getPriceAndRoute(currentOrder.value?.toOrderForm()!!)
            val data =
                driverRepository.getDriverLocation(driverInfo.value?.userId ?: "")
            if (data == null) return@launch
            driverLocationResponse = data
            val listResponse =
                addressRepository.getDirectionData(
                    AddressInfo(
                        latitude = driverLocationResponse?.latitude!!,
                        longitude = driverLocationResponse?.longitude!!
                    ),
                    listData
                )
            _directionResponses.value = listResponse
        }
    }

    fun sendFCM(token: String) {
        viewModelScope.launch {
            repository.sendFCM(token)
        }
    }

    suspend fun getDriverInfo(id: String): DriverResponse? {
        if (id.isEmpty()) return null
        val data = driverRepository.getDriverInfo(id)
        _driverInfo.value = data
        return data
    }
    private var pollingJob: Job? = null
    suspend fun getListRoute(): List<PriceRouteItem> = withContext(Dispatchers.IO) {
        orderRepository.getPriceAndRoute(currentOrder.value?.toOrderForm()!!)
    }
    fun startPolling(orderId: String) {
        stopPolling()

        pollingJob = viewModelScope.launch(Dispatchers.IO) {
            while (isLoop) {
                try {
                    _currentOrder.value = orderRepository.getOrderDetail(orderId)
                    Log.d("dsd", _currentOrder.value?.id ?: "")
                    if (_currentOrder.value?.status != OrderStatus.FINDING_DRIVER)
                        getDirection()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(5000)
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    fun getSelectedOrder(orderId: String) {
        viewModelScope.launch {
            orderRepository.getOrderDetail(orderId)?.let {
                _selectedOrder.emit(it)
            }
        }
    }

    fun loadUser() = viewModelScope.launch {
        application
        repository.getUserByIdFromSever(JwtUtils.getUserId(application) ?: "")?.let {
            _user.value = it.toUser()
        }
    }

    fun loadListOrder() {
        viewModelScope.launch {
            showLoading()
            val result = orderRepository.getListOrder(1, 50, "createdAt,desc")
            result.onSuccess {
                _listOrder.value = it
                hideLoading()
            }
            result.onFailure {
                _listOrder.value = emptyList()
                hideLoading()
            }
        }
    }

    fun getDetailOrder(position: Int): OrderItem {
        return _listOrder.value[position]
    }
}
