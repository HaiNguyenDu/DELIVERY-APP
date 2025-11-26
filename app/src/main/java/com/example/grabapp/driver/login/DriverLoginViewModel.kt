package com.example.grabapp.driver.login

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.example.grabapp.base.BaseViewModel
import com.example.grabapp.data.repository.AIServiceRepository
import com.example.grabapp.data.repository.AuthRepository
import com.example.grabapp.extention.toMultipartBodyPart
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import com.google.gson.JsonParser
import java.io.IOException

class DriverLoginViewModel(
    application: Application,
    private val authRepository: AuthRepository,
    private val aiServiceRepository: AIServiceRepository
) : BaseViewModel(application) {
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _loginEvent = MutableSharedFlow<Boolean>(replay = 0)
    val loginEvent = _loginEvent.asSharedFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _faceVerifyState = MutableStateFlow<FaceVerifyState>(FaceVerifyState.Idle)
    val faceVerifyState = _faceVerifyState.asStateFlow()

    fun login(phone: String, password: String) {
        if (phone.isBlank() || password.isBlank()) {
            _errorMessage.value = "Vui lòng nhập số điện thoại và mật khẩu"
            return
        }
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null

            when (val res = authRepository.login(phone, password)) {
                is AuthRepository.LoginResult.Success -> {
                    _loading.value = false
                    onLoginSuccess()
                }

                is AuthRepository.LoginResult.Error -> {
                    _loading.value = false
                    val msg = when (res.code) {
                        401 -> "Số điện thoại hoặc mật khẩu không đúng"
                        else -> res.message
                    }
                    _errorMessage.value = msg
                }
            }
        }
    }

    fun verifyFace(bitmap: Bitmap, userId: String) {
        viewModelScope.launch {
            try {
                _faceVerifyState.value = FaceVerifyState.Verifying
                showLoading()

                val filePart = bitmap.toMultipartBodyPart(getApplication(), "file")
                    ?: run {
                        _faceVerifyState.value = FaceVerifyState.Error("Không thể đọc file ảnh")
                        hideLoading()
                        return@launch
                    }

                val uidRequestBody = userId.toRequestBody("text/plain".toMediaTypeOrNull())

                val response = aiServiceRepository.verifyFace(uidRequestBody, filePart)
                hideLoading()

                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.success == true) {
                        val match = result.data?.match == true
                        if (match) {
                            _faceVerifyState.value = FaceVerifyState.Success
                            onLoginSuccess()
                        } else {
                            _faceVerifyState.value = FaceVerifyState.Error("Khuôn mặt không khớp. Vui lòng thử lại.")
                        }
                    } else {
                        _faceVerifyState.value = FaceVerifyState.Error("Xác thực thất bại")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = if (!errorBody.isNullOrEmpty()) {
                        try {
                            // Thử parse JSON error response
                            val jsonObject = JsonParser.parseString(errorBody).asJsonObject
                            jsonObject.get("message")?.asString?.takeIf { it.isNotEmpty() }
                                ?: jsonObject.get("error")?.asString
                                ?: "Xác thực thất bại: HTTP ${response.code()}"
                        } catch (e: Exception) {
                            // Nếu không parse được JSON, dùng errorBody trực tiếp
                            if (errorBody.length > 200) {
                                "Xác thực thất bại: HTTP ${response.code()}"
                            } else {
                                errorBody
                            }
                        }
                    } else {
                        "Xác thực thất bại: HTTP ${response.code()}"
                    }
                    _faceVerifyState.value = FaceVerifyState.Error(errorMsg)
                }
            } catch (e: IOException) {
                hideLoading()
                _faceVerifyState.value = FaceVerifyState.Error("Lỗi kết nối: ${e.message}")
            } catch (e: Exception) {
                hideLoading()
                _faceVerifyState.value = FaceVerifyState.Error("Lỗi không xác định: ${e.message}")
            }
        }
    }

    private fun onLoginSuccess() {
        viewModelScope.launch { _loginEvent.emit(true) }
    }
}

sealed class FaceVerifyState {
    object Idle : FaceVerifyState()
    object Verifying : FaceVerifyState()
    object Success : FaceVerifyState()
    data class Error(val message: String) : FaceVerifyState()
}
