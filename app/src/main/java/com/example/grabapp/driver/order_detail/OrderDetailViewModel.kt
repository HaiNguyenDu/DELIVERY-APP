package com.example.grabapp.driver.order_detail

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.grabapp.base.BaseViewModel
import com.example.grabapp.data.model.OrderResponse
import com.example.grabapp.data.repository.AddressRepository
import com.example.grabapp.data.repository.OrderRepository
import com.example.grabapp.model.Address
import com.example.grabapp.model.Order
import com.example.grabapp.model.OrderState
import com.example.grabapp.model.OrderStatus
import com.example.grabapp.model.PackageStatus
import com.example.grabapp.respone.Coordinates
import com.example.grabapp.respone.GoongDirectionApiResponse
import com.example.grabapp.model.DeliveryAddressItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderDetailViewModel(
    application: Application,
    private val orderRepository: OrderRepository,
    private val addressRepository: AddressRepository
) : BaseViewModel(application) {

    private val _order = MutableStateFlow<Order?>(null)
    val order = _order.asStateFlow()

    private val _addressItems = MutableStateFlow<List<DeliveryAddressItem>>(emptyList())
    val addressItems = _addressItems.asStateFlow()

    private val _currentState = MutableStateFlow<OrderState>(OrderState.RECEIVED_ORDER)
    val currentState = _currentState.asStateFlow()

    private val _orderResponse = MutableStateFlow<OrderResponse?>(null)
    val orderResponse = _orderResponse.asStateFlow()

    private val _orderStatus = MutableStateFlow<OrderStatus?>(null)
    val orderStatus = _orderStatus.asStateFlow()

    private val _positioning = MutableStateFlow<Address?>(null)
    val positioning = _positioning.asStateFlow()

    private val _directionResponse = MutableStateFlow<GoongDirectionApiResponse?>(null)
    val directionResponse = _directionResponse.asStateFlow()
    
    private val _isDeliveryCompleteEnabled = MutableStateFlow(false)
    val isDeliveryCompleteEnabled = _isDeliveryCompleteEnabled.asStateFlow()

    fun initializeOrder(order: Order) {
        _order.value = order
        _currentState.value = OrderState.RECEIVED_ORDER
        loadOrderDetails(order.orderId)
    }

    fun loadOrderDetails(orderId: String) {
        viewModelScope.launch {
            try {
                showLoading()
                val result = orderRepository.getOrderById(orderId)
                when (result) {
                    is OrderRepository.OrderResult.Success -> {
                        _orderResponse.value = result.response
                        _orderStatus.value = parseOrderStatus(result.response.status)
                        val addressItems = createAddressListFromOrderResponse(result.response)
                        _addressItems.value = addressItems
                        checkAndUpdateDeliveryCompleteButton()
                    }
                    is OrderRepository.OrderResult.Error -> {
                        _order.value?.let { order ->
                            val addressItems = createAddressListFromOrder(order)
                            _addressItems.value = addressItems
                        }
                    }
                }
            } catch (e: Exception) {
                _order.value?.let { order ->
                    val addressItems = createAddressListFromOrder(order)
                    _addressItems.value = addressItems
                }
            } finally {
                hideLoading()
            }
        }
    }

    private fun createAddressListFromOrderResponse(orderResponse: OrderResponse): List<DeliveryAddressItem> {
        val items = mutableListOf<DeliveryAddressItem>()

        // Add pickup address first
        items.add(
            DeliveryAddressItem(
                name = orderResponse.pickupAddress.name,
                address = orderResponse.pickupAddress.detail,
                isPickup = true,
                packageId = null,
                packageStatus = null
            )
        )

        // Sort packages by routeIndex from priceAndRoutes
        val sortedPackages = if (orderResponse.priceAndRoutes.isNotEmpty()) {
            // Sort priceAndRoutes by routeIndex to get delivery order
            val sortedRoutes = orderResponse.priceAndRoutes.sortedBy { it.routeIndex }
            
            // Match each route with corresponding package by coordinates
            sortedRoutes.mapNotNull { route ->
                orderResponse.packages.firstOrNull { packageInfo ->
                    // Match by coordinates with small tolerance for floating point comparison
                    kotlin.math.abs(route.latitude - packageInfo.dropoffAddress.latitude) < 0.0001 &&
                    kotlin.math.abs(route.longitude - packageInfo.dropoffAddress.longitude) < 0.0001
                }
            }
        } else {
            // Fallback to original order if no priceAndRoutes
            orderResponse.packages
        }

        // Add sorted dropoff addresses
        sortedPackages.forEach { packageInfo ->
            val packageStatus = packageInfo.packageStatus?.let { parsePackageStatus(it) }
            items.add(
                DeliveryAddressItem(
                    name = packageInfo.dropoffAddress.name,
                    address = packageInfo.dropoffAddress.detail,
                    isPickup = false,
                    packageId = packageInfo.id,
                    packageStatus = packageStatus
                )
            )
        }

        return items
    }
    
    private fun parseOrderStatus(status: String): OrderStatus? {
        return try {
            OrderStatus.valueOf(status)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    
    private fun parsePackageStatus(status: String): PackageStatus? {
        return try {
            PackageStatus.valueOf(status)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    
    private fun checkAndUpdateDeliveryCompleteButton() {
        val orderResponse = _orderResponse.value ?: return
        val allDelivered = orderResponse.packages.all { 
            it.packageStatus == PackageStatus.DELIVERED.name 
        }
        _isDeliveryCompleteEnabled.value = allDelivered && 
            _orderStatus.value != OrderStatus.DELIVERED
    }

    private fun createAddressListFromOrder(order: Order): List<DeliveryAddressItem> {
        return listOf(
            DeliveryAddressItem(
                name = order.pickerName,
                address = order.pickerAddress,
                isPickup = true
            ),
            DeliveryAddressItem(
                name = order.deliveryName,
                address = order.deliveryAddress,
                isPickup = false
            )
        )
    }

    fun moveToNextState() {
        val nextState = when (_currentState.value) {
            OrderState.RECEIVED_ORDER -> OrderState.COMING_TO_PICKUP
            OrderState.COMING_TO_PICKUP -> OrderState.RECEIVED_GOODS
            OrderState.RECEIVED_GOODS -> OrderState.DELIVERING
            OrderState.DELIVERING -> OrderState.DELIVERED
            OrderState.DELIVERED -> return
            OrderState.CANCELED -> return
        }
        _currentState.value = nextState
    }

    fun getPickUpAddress(): Address {
        val order = _order.value ?: return getDefaultPickupAddress()
        val addressItems = _addressItems.value
        val pickupItem = addressItems.firstOrNull { it.isPickup }
        return if (pickupItem != null) {
            order.pickupCoordinates?.let { coordinates ->
                Address(
                    coordinates = coordinates,
                    name = pickupItem.name,
                    address = pickupItem.address
                )
            } ?: Address(
                coordinates = Coordinates(16.07367333700006, 108.14992938100005),
                name = pickupItem.name,
                address = pickupItem.address
            )
        } else {
            order.pickupCoordinates?.let { coordinates ->
                Address(
                    coordinates = coordinates,
                    name = order.pickerName,
                    address = order.pickerAddress
                )
            } ?: Address(
                coordinates = Coordinates(16.07367333700006, 108.14992938100005),
                name = order.pickerName,
                address = order.pickerAddress
            )
        }
    }

    fun getDropOffAddress(): Address {
        val order = _order.value ?: return getDefaultDropoffAddress()
        val addressItems = _addressItems.value
        val dropoffItem = addressItems.firstOrNull { !it.isPickup }
        return if (dropoffItem != null) {
            order.dropoffCoordinates?.let { coordinates ->
                Address(
                    coordinates = coordinates,
                    name = dropoffItem.name,
                    address = dropoffItem.address
                )
            } ?: Address(
                coordinates = Coordinates(16.07079150000004, 108.14888825800006),
                name = dropoffItem.name,
                address = dropoffItem.address
            )
        } else {
            order.dropoffCoordinates?.let { coordinates ->
                Address(
                    coordinates = coordinates,
                    name = order.deliveryName,
                    address = order.deliveryAddress
                )
            } ?: Address(
                coordinates = Coordinates(16.07079150000004, 108.14888825800006),
                name = order.deliveryName,
                address = order.deliveryAddress
            )
        }
    }

    private fun getDefaultPickupAddress(): Address {
        return Address(
            coordinates = Coordinates(16.07367333700006, 108.14992938100005),
            name = "",
            address = ""
        )
    }

    private fun getDefaultDropoffAddress(): Address {
        return Address(
            coordinates = Coordinates(16.07079150000004, 108.14888825800006),
            name = "",
            address = ""
        )
    }

    fun getCurrentLocation(onSuccess: (Address) -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                addressRepository.getCurrentLocation()?.let { location ->
                    val address = Address(
                        coordinates = Coordinates(location.latitude, location.longitude),
                        name = "Vị trí hiện tại",
                        address = "Vị trí hiện tại của tài xế"
                    )
                    _positioning.value = address
                    onSuccess(address)
                } ?: onError(Exception("Không thể lấy vị trí hiện tại"))
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun getDirectionToAddress(
        destination: Address,
        onSuccess: (GoongDirectionApiResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val startAddress = _positioning.value
        if (startAddress == null) {
            onError("Chưa có vị trí hiện tại")
            return
        }

        addressRepository.getDirectionData(
            dropOff = destination,
            pickUp = startAddress,
            onSuccess = { response ->
                _directionResponse.value = response
                onSuccess(response)
            },
            onError = { error ->
                onError(error)
            }
        )
    }
    
    fun updateOrderStatus(newStatus: OrderStatus, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val orderId = _orderResponse.value?.id ?: _order.value?.orderId
        if (orderId == null) {
            onError("Không tìm thấy order ID")
            return
        }
        
        viewModelScope.launch {
            try {
                showLoading()
                val result = orderRepository.updateOrderStatus(orderId, newStatus.name)
                when (result) {
                    is OrderRepository.OrderResult.Success -> {
                        _orderResponse.value = result.response
                        _orderStatus.value = parseOrderStatus(result.response.status)
                        val addressItems = createAddressListFromOrderResponse(result.response)
                        _addressItems.value = addressItems
                        checkAndUpdateDeliveryCompleteButton()
                        onSuccess()
                    }
                    is OrderRepository.OrderResult.Error -> {
                        onError(result.message)
                    }
                }
            } catch (e: Exception) {
                onError(e.message ?: "Lỗi không xác định")
            } finally {
                hideLoading()
            }
        }
    }
    
    fun updatePackageStatus(
        packageId: String,
        newStatus: PackageStatus,
        note: String? = null,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                showLoading()
                val result = orderRepository.updatePackageStatus(packageId, newStatus.name, note)
                when (result) {
                    is OrderRepository.PackageResult.Success -> {
                        // Reload order details to get updated status
                        _orderResponse.value?.id?.let { orderId ->
                            val reloadResult = orderRepository.getOrderById(orderId)
                            when (reloadResult) {
                                is OrderRepository.OrderResult.Success -> {
                                    _orderResponse.value = reloadResult.response
                                    _orderStatus.value = parseOrderStatus(reloadResult.response.status)
                                    val addressItems = createAddressListFromOrderResponse(reloadResult.response)
                                    _addressItems.value = addressItems
                                    checkAndUpdateDeliveryCompleteButton()
                                    onSuccess()
                                }
                                is OrderRepository.OrderResult.Error -> {
                                    onError(reloadResult.message)
                                }
                            }
                        } ?: onSuccess()
                    }
                    is OrderRepository.PackageResult.Error -> {
                        onError(result.message)
                    }
                }
            } catch (e: Exception) {
                onError(e.message ?: "Lỗi không xác định")
            } finally {
                hideLoading()
            }
        }
    }
    
    fun handlePickupAddressClick() {
        val currentStatus = _orderStatus.value
        when (currentStatus) {
            OrderStatus.DRIVER_ASSIGNED -> {
                updateOrderStatus(OrderStatus.DRIVER_EN_ROUTE_PICKUP, {
                    val addressItems = createAddressListFromOrderResponse(_orderResponse.value!!)
                    _addressItems.value = addressItems
                }, {})
            }
            OrderStatus.DRIVER_EN_ROUTE_PICKUP -> {
                updateOrderStatus(OrderStatus.ARRIVED_PICKUP, {
                    val addressItems = createAddressListFromOrderResponse(_orderResponse.value!!)
                    _addressItems.value = addressItems
                }, {})
            }
            OrderStatus.ARRIVED_PICKUP -> {
                updateOrderStatus(OrderStatus.PACKAGE_PICKED, {
                    val packages = _orderResponse.value?.packages ?: emptyList()
                    if (packages.isNotEmpty()) {
                        var completedCount = 0
                        packages.forEach { packageInfo ->
                            updatePackageStatus(
                                packageInfo.id,
                                PackageStatus.PICKED_UP,
                                null,
                                {
                                    completedCount++
                                    if (completedCount == packages.size) {
                                        val addressItems = createAddressListFromOrderResponse(_orderResponse.value!!)
                                        _addressItems.value = addressItems
                                    }
                                },
                                {}
                            )
                        }
                    }
                }, {})
            }
            else -> {}
        }
    }
    
    fun handleDropoffAddressClick(packageId: String, currentPackageStatus: PackageStatus?) {
        when (currentPackageStatus) {
            PackageStatus.PICKED_UP -> {
                if (_orderStatus.value != OrderStatus.EN_ROUTE_DELIVERY) {
                    updateOrderStatus(OrderStatus.EN_ROUTE_DELIVERY, {
                        updatePackageStatus(packageId, PackageStatus.DELIVERY_IN_PROGRESS, null, {
                            val addressItems = createAddressListFromOrderResponse(_orderResponse.value!!)
                            _addressItems.value = addressItems
                        }, {})
                    }, {})
                } else {
                    updatePackageStatus(packageId, PackageStatus.DELIVERY_IN_PROGRESS, null, {
                        val addressItems = createAddressListFromOrderResponse(_orderResponse.value!!)
                        _addressItems.value = addressItems
                    }, {})
                }
            }
            PackageStatus.DELIVERY_IN_PROGRESS -> {
                updatePackageStatus(packageId, PackageStatus.DELIVERED, null, {
                    val addressItems = createAddressListFromOrderResponse(_orderResponse.value!!)
                    _addressItems.value = addressItems
                    checkAndUpdateDeliveryCompleteButton()
                }, {})
            }
            else -> {}
        }
    }
    
    fun handleDeliveryComplete() {
    }
}
