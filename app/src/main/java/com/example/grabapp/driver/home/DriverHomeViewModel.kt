package com.example.grabapp.driver.home

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.grabapp.base.BaseViewModel
import com.example.grabapp.data.model.UploadFaceRequest
import com.example.grabapp.data.repository.AIServiceRepository
import com.example.grabapp.data.repository.FileRepository
import com.example.grabapp.extention.toMultipartBodyPart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class DriverHomeViewModel(
    application: Application,
    private val fileRepository: FileRepository,
    private val aiServiceRepository: AIServiceRepository
) : BaseViewModel(application) {
    private val _uploadState = MutableStateFlow<FaceUploadState>(FaceUploadState.Idle)
    val uploadState = _uploadState.asStateFlow()

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
}

sealed class FaceUploadState {
    object Idle : FaceUploadState()
    object PickingImage : FaceUploadState()
    object UploadingFile : FaceUploadState()
    object UploadingToAI : FaceUploadState()
    data class Success(val imageUrl: String) : FaceUploadState()
    data class Error(val message: String) : FaceUploadState()
}
