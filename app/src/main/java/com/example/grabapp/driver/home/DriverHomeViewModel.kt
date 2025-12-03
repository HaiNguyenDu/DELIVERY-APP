package com.example.grabapp.driver.home

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.grabapp.base.BaseViewModel
import com.example.grabapp.data.TokenStorage
import com.example.grabapp.data.model.UploadFaceRequest
import com.example.grabapp.data.repository.AIServiceRepository
import com.example.grabapp.data.repository.FileRepository
import com.example.grabapp.data.repository.OrderRepository
import com.example.grabapp.extention.toMultipartBodyPart
import com.example.grabapp.model.Order
import com.example.grabapp.util.OrderMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class DriverHomeViewModel(
    application: Application,
    private val fileRepository: FileRepository,
    private val aiServiceRepository: AIServiceRepository,
    private val orderRepository: OrderRepository
) : BaseViewModel(application) {
    private val _uploadState = MutableStateFlow<FaceUploadState>(FaceUploadState.Idle)
    val uploadState = _uploadState.asStateFlow()
    
    private val tokenStorage = TokenStorage(getApplication())
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders = _orders.asStateFlow()
    
    private val _orderLoadState = MutableStateFlow<OrderLoadState>(OrderLoadState.Idle)
    val orderLoadState = _orderLoadState.asStateFlow()

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
