package com.example.grabapp.driver.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.grabapp.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DriverHomeViewModel(
    application: Application
) : BaseViewModel(application) {
    private val _uploadState = MutableStateFlow<FaceUploadState>(FaceUploadState.Idle)
    val uploadState = _uploadState.asStateFlow()

    fun uploadDriverFace(imageUri: String, userId: String) {
        viewModelScope.launch {
            _uploadState.value = FaceUploadState.PickingImage
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
