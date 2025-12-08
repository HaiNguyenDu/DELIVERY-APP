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

    private val _positioning = MutableStateFlow<Address?>(null)
    val positioning = _positioning.asStateFlow()

    private val _directionResponse = MutableStateFlow<GoongDirectionApiResponse?>(null)
    val directionResponse = _directionResponse.asStateFlow()

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
                        val addressItems = createAddressListFromOrderResponse(result.response)
                        _addressItems.value = addressItems
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

        items.add(
            DeliveryAddressItem(
                name = orderResponse.pickupAddress.name,
                address = orderResponse.pickupAddress.detail,
                isPickup = true
            )
        )

        orderResponse.packages.forEach { packageInfo ->
            items.add(
                DeliveryAddressItem(
                    name = packageInfo.dropoffAddress.name,
                    address = packageInfo.dropoffAddress.detail,
                    isPickup = false
                )
            )
        }

        return items
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
}
