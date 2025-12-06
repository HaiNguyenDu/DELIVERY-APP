package com.example.grabapp.ui.login

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.grabapp.base.BaseViewModel
import com.example.grabapp.data.repository.AuthRepositoryImpl
import com.example.grabapp.domain.repository.AuthRepository
import com.example.grabapp.utils.SharedPreferencesUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) :
    BaseViewModel(application) {
    val authRepositoryImpl: AuthRepository = AuthRepositoryImpl()
    private val _currentFragmentIndex = MutableStateFlow(0)
    val currentFragmentIndex: StateFlow<Int> = _currentFragmentIndex

    private val _phoneNumber = MutableStateFlow("")

    val phoneNumber: StateFlow<String> = _phoneNumber

    private val _password: MutableStateFlow<String> = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isLogin = MutableStateFlow<Boolean>(true)

    fun setPassword(password: String) {
        viewModelScope.launch {
            _password.value = password
        }
    }

    fun replaceFragment(index: Int) {
        viewModelScope.launch {
            _currentFragmentIndex.emit(index)
        }
    }

    fun getIsLogin(): Boolean {
        return _isLogin.value
    }

    fun setIsLogin(value: Boolean) {
        _isLogin.value = value
    }

    fun setPhoneNumber(phoneNumber: String) {
        viewModelScope.launch {
            _phoneNumber.emit(phoneNumber)
        }
    }

    fun getPhoneNumber(): String {
        return _phoneNumber.value
    }

    fun login(onSuccess: Runnable, onFail: Runnable) {
        showLoading()
        viewModelScope.launch {
            val result = authRepositoryImpl.login(phoneNumber.value, password.value)
            result.onSuccess {
                onSuccess.run()
                hideLoading()
                SharedPreferencesUtils(getApplication()).putToken(it.accessToken)
                SharedPreferencesUtils(getApplication()).putRefreshToken(it.refreshToken)
            }.onFailure {
                onFail.run()
                hideLoading()
            }
        }
    }
}