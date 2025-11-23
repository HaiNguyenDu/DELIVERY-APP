package com.example.grabapp.driver.login

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.grabapp.base.BaseViewModel
import com.example.grabapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DriverLoginViewModel(
    application: Application,
    private val authRepository: AuthRepository
) : BaseViewModel(application) {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _loginEvent = MutableSharedFlow<Boolean>(replay = 0)
    val loginEvent = _loginEvent.asSharedFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun login(phone: String, password: String) {
        if (phone.isBlank() || password.isBlank()) {
            _errorMessage.value = "Vui lòng nhập số điện thoại và mật khẩu"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            when (val res = authRepository.login(phone, password)) {
                is AuthRepository.LoginResult.Success -> {
                    _isLoading.value = false
                    onLoginSuccess()
                }

                is AuthRepository.LoginResult.Error -> {
                    _isLoading.value = false
                    val msg = when (res.code) {
                        401 -> "Số điện thoại hoặc mật khẩu không đúng"
                        else -> res.message
                    }
                    _errorMessage.value = msg
                }
            }
        }
    }

    private fun onLoginSuccess() {
        viewModelScope.launch { _loginEvent.emit(true) }
    }
}
