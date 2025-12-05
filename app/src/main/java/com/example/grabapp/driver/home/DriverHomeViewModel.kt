package com.example.grabapp.driver.home

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.grabapp.base.BaseViewModel
import com.example.grabapp.data.ConnectionStorage
import com.example.grabapp.data.TokenStorage
import com.example.grabapp.data.model.UpdateDriverStatusRequest
import com.example.grabapp.data.model.UploadFaceRequest
import com.example.grabapp.data.model.DriverRegisterResponse
import com.example.grabapp.data.repository.AIServiceRepository
import com.example.grabapp.data.repository.DriverRepository
import com.example.grabapp.data.repository.FileRepository
import com.example.grabapp.data.repository.OrderRepository
import com.example.grabapp.driver.home.data.ConnectionState
import com.example.grabapp.util.FCMTokenHelper
import com.example.grabapp.util.LocationUpdateManager
import com.example.grabapp.extention.toMultipartBodyPart
import com.example.grabapp.model.Order
import com.example.grabapp.model.OrderState
import com.example.grabapp.util.OrderMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DriverHomeViewModel(
    application: Application,
    private val fileRepository: FileRepository,
    private val aiServiceRepository: AIServiceRepository,
    private val orderRepository: OrderRepository
) : BaseViewModel(application) {
    private val _uploadState = MutableStateFlow<FaceUploadState>(FaceUploadState.Idle)
    val uploadState = _uploadState.asStateFlow()
    
    private val tokenStorage = TokenStorage(getApplication())
    private val connectionStorage = ConnectionStorage(getApplication())
    private val driverRepository = DriverRepository(getApplication())
    private val locationUpdateManager = LocationUpdateManager.getInstance(getApplication())
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders = _orders.asStateFlow()
    
    private val _orderLoadState = MutableStateFlow<OrderLoadState>(OrderLoadState.Idle)
    val orderLoadState = _orderLoadState.asStateFlow()
    
    private val _driverInfo = MutableStateFlow<DriverRegisterResponse?>(null)
    val driverInfo = _driverInfo.asStateFlow()

    private val _updateStatusState = MutableStateFlow<UpdateStatusState>(UpdateStatusState.Idle)
    val updateStatusState = _updateStatusState.asStateFlow()

    fun uploadDriverFace(uri: Uri, userId: String) {
        viewModelScope.launch {
            try {
                _uploadState.value = FaceUploadState.PickingImage
                
                val filePart = uri.toMultipartBodyPart(getApplication(), "file")
                    ?: run {
                        _uploadState.value = FaceUploadState.Error("Không thể đọc file ảnh")
                        return@launch
                    }
                
                val uidRequestBody = userId.toRequestBody("text/plain".toMediaTypeOrNull())
                
                _uploadState.value = FaceUploadState.UploadingFile
                showLoading()
                
                val uploadResponse = fileRepository.uploadFile(uidRequestBody, filePart)
                
                if (uploadResponse.isSuccessful) {
                    val uploadResult = uploadResponse.body()
                    val imageUrl = uploadResult?.url
                    
                    if (imageUrl != null) {
                        _uploadState.value = FaceUploadState.UploadingToAI
                        
                        val faceRequest = UploadFaceRequest(listOf(imageUrl))
                        val faceResponse = aiServiceRepository.uploadFace(userId, faceRequest)
                        
                        hideLoading()
                        
                        if (faceResponse.isSuccessful) {
                            _uploadState.value = FaceUploadState.Success(imageUrl)
                        } else {
                            val errorMsg = faceResponse.errorBody()?.string() 
                                ?: "Lỗi khi upload ảnh lên AI service"
                            _uploadState.value = FaceUploadState.Error(errorMsg)
                        }
                    } else {
                        hideLoading()
                        _uploadState.value = FaceUploadState.Error("Không nhận được URL từ server")
                    }
                } else {
                    hideLoading()
                    val errorMsg = uploadResponse.errorBody()?.string() 
                        ?: "Lỗi khi upload file: HTTP ${uploadResponse.code()}"
                    _uploadState.value = FaceUploadState.Error(errorMsg)
                }
            } catch (e: IOException) {
                hideLoading()
                _uploadState.value = FaceUploadState.Error("Lỗi kết nối: ${e.message}")
            } catch (e: Exception) {
                hideLoading()
                _uploadState.value = FaceUploadState.Error("Lỗi không xác định: ${e.message}")
            }
        }
    }

    fun fetchOrders() {
        viewModelScope.launch {
            try {
                val userId = tokenStorage.getUserId()
                if (userId.isNullOrEmpty()) {
                    _orderLoadState.value = OrderLoadState.Error("Không tìm thấy thông tin người dùng")
                    return@launch
                }

                _orderLoadState.value = OrderLoadState.Loading
                val result = orderRepository.getOrders("DRIVER", userId)

                when (result) {
                    is OrderRepository.OrderListResult.Success -> {
                        val mappedOrders = result.response.content.map { orderResponse ->
                            OrderMapper.mapToOrder(orderResponse)
                        }
                        _orders.value = mappedOrders
                        _orderLoadState.value = OrderLoadState.Success
                    }
                    is OrderRepository.OrderListResult.Error -> {
                        _orderLoadState.value = OrderLoadState.Error(
                            result.message ?: "Không thể tải danh sách đơn hàng"
                        )
                    }
                }
            } catch (e: Exception) {
                _orderLoadState.value = OrderLoadState.Error("Lỗi: ${e.message ?: "Không xác định"}")
            }
        }
    }
    
    fun fetchDriverInfo() {
        viewModelScope.launch {
            try {
                val userId = tokenStorage.getUserId()
                if (userId.isNullOrBlank()) {
                    return@launch
                }

                when (val result = driverRepository.getDriverInfo(userId)) {
                    is DriverRepository.DriverInfoResult.Success -> {
                        _driverInfo.value = result.response
                    }
                    is DriverRepository.DriverInfoResult.NotFound -> {
                    }
                    is DriverRepository.DriverInfoResult.Error -> {
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getTodayOrdersCount(): Int {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val todayCalendar = Calendar.getInstance().apply {
            time = today.time
        }
        
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        
        return _orders.value.count { order ->
            try {
                val orderDate = dateFormat.parse(order.orderTime)
                orderDate?.let {
                    val orderCalendar = Calendar.getInstance().apply {
                        time = it
                    }
                    orderCalendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR) &&
                    orderCalendar.get(Calendar.MONTH) == todayCalendar.get(Calendar.MONTH) &&
                    orderCalendar.get(Calendar.DAY_OF_MONTH) == todayCalendar.get(Calendar.DAY_OF_MONTH)
                } ?: false
            } catch (e: Exception) {
                false
            }
        }
    }
    
    fun getTotalCompletedIncome(): Long {
        return _orders.value
            .filter { it.orderState == OrderState.DELIVERED }
            .sumOf { it.income }
    }

    fun updateDriverStatus(isAvailable: Boolean) {
        viewModelScope.launch {
            try {
                _updateStatusState.value = UpdateStatusState.Updating
                
                val fcmToken = FCMTokenHelper.getFCMToken(getApplication())
                val request = UpdateDriverStatusRequest(
                    isAvailable = isAvailable,
                    fcmToken = fcmToken
                )

                when (val result = driverRepository.updateDriverStatus(request)) {
                    is DriverRepository.UpdateStatusResult.Success -> {
                        _updateStatusState.value = UpdateStatusState.Success
                        val connectionState = if (isAvailable) ConnectionState.CONNECTED else ConnectionState.DISCONNECTED
                        connectionStorage.saveConnectionState(connectionState)
                        
                        // Quản lý location updates dựa trên connection state
                        if (isAvailable) {
                            locationUpdateManager.startLocationUpdates()
                        } else {
                            locationUpdateManager.stopLocationUpdates()
                        }
                    }
                    is DriverRepository.UpdateStatusResult.Error -> {
                        _updateStatusState.value = UpdateStatusState.Error(
                            result.message ?: "Không thể cập nhật trạng thái"
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _updateStatusState.value = UpdateStatusState.Error(
                    "Lỗi: ${e.message ?: "Không xác định"}"
                )
            }
        }
    }

    fun getSavedConnectionState(): ConnectionState {
        return connectionStorage.getConnectionState()
    }
    
    fun findOrderById(orderId: String): Order? {
        Log.d("DriverHomeViewModel", "=== findOrderById ===")
        Log.d("DriverHomeViewModel", "Tìm order với ID: $orderId")
        Log.d("DriverHomeViewModel", "Số lượng orders hiện tại: ${_orders.value.size}")
        Log.d("DriverHomeViewModel", "Danh sách order IDs: ${_orders.value.map { it.orderId }}")
        
        val foundOrder = _orders.value.firstOrNull { it.orderId == orderId }
        if (foundOrder != null) {
            Log.d("DriverHomeViewModel", "Tìm thấy order: ${foundOrder.orderId}")
        } else {
            Log.w("DriverHomeViewModel", "Không tìm thấy order với ID: $orderId")
        }
        return foundOrder
    }
    
    fun fetchOrderById(orderId: String, onOrderFound: (Order?) -> Unit) {
        Log.d("DriverHomeViewModel", "=== fetchOrderById ===")
        Log.d("DriverHomeViewModel", "OrderID: $orderId")
        
        viewModelScope.launch {
            try {
                Log.d("DriverHomeViewModel", "Calling API getOrderById với orderId: $orderId")
                val result = orderRepository.getOrderById(orderId)
                when (result) {
                    is OrderRepository.OrderResult.Success -> {
                        Log.d("DriverHomeViewModel", "Fetch order by id thành công")
                        val mappedOrder = OrderMapper.mapToOrder(result.response)
                        
                        // Cập nhật distance và estimatedTime cho dialog
                        val dialogDistance = OrderMapper.formatDistanceForDialog(result.response.priceAndRoutes)
                        val dialogTime = OrderMapper.formatTimeForDialog(result.response.priceAndRoutes)
                        
                        // Tạo Order mới với distance và time đã format cho dialog
                        val orderForDialog = mappedOrder.copy(
                            distance = dialogDistance,
                            estimatedTime = dialogTime
                        )
                        
                        Log.d("DriverHomeViewModel", "Order mapped thành công: ${orderForDialog.orderId}")
                        onOrderFound(orderForDialog)
                    }
                    is OrderRepository.OrderResult.Error -> {
                        Log.e("DriverHomeViewModel", "Lỗi khi fetch order by id: ${result.message}")
                        onOrderFound(null)
                    }
                }
            } catch (e: Exception) {
                Log.e("DriverHomeViewModel", "Exception khi fetch order by id: ${e.message}", e)
                onOrderFound(null)
            }
        }
    }

    fun handleAppKilled() {
        viewModelScope.launch {
            try {
                // Dừng location updates khi app bị kill
                locationUpdateManager.stopLocationUpdates()
                
                val currentState = connectionStorage.getConnectionState()
                if (currentState == ConnectionState.CONNECTED) {
                    val fcmToken = FCMTokenHelper.getFCMToken(getApplication())
                    val request = UpdateDriverStatusRequest(
                        isAvailable = false,
                        fcmToken = fcmToken
                    )
                    driverRepository.updateDriverStatus(request)
                    connectionStorage.saveConnectionState(ConnectionState.DISCONNECTED)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                connectionStorage.saveConnectionState(ConnectionState.DISCONNECTED)
            }
        }
    }
    
    /**
     * Khởi động location updates nếu connection state là CONNECTED
     * Được gọi khi ViewModel được tạo hoặc khi app resume
     */
    fun startLocationUpdatesIfConnected() {
        val currentState = connectionStorage.getConnectionState()
        if (currentState == ConnectionState.CONNECTED) {
            locationUpdateManager.startLocationUpdates()
        }
    }
    
    /**
     * Dừng location updates
     */
    fun stopLocationUpdates() {
        locationUpdateManager.stopLocationUpdates()
    }
}

sealed class UpdateStatusState {
    object Idle : UpdateStatusState()
    object Updating : UpdateStatusState()
    object Success : UpdateStatusState()
    data class Error(val message: String) : UpdateStatusState()
}

sealed class FaceUploadState {
    object Idle : FaceUploadState()
    object PickingImage : FaceUploadState()
    object UploadingFile : FaceUploadState()
    object UploadingToAI : FaceUploadState()
    data class Success(val imageUrl: String) : FaceUploadState()
    data class Error(val message: String) : FaceUploadState()
}

sealed class OrderLoadState {
    object Idle : OrderLoadState()
    object Loading : OrderLoadState()
    object Success : OrderLoadState()
    data class Error(val message: String) : OrderLoadState()
}
