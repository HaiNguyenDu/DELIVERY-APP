package com.example.grabapp.base;

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grabapp.model.Message
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    private val _isLoading = MutableStateFlow(false)

    internal val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _message = MutableSharedFlow<Message>()

    internal val getMessage: SharedFlow<Message> get() = _message

    fun showLoading() {
        _isLoading.value = true
    }

    fun hideLoading() {
        _isLoading.value = false
    }

    protected fun emitMessage(message: String) {
        viewModelScope.launch {
            _message.emit(Message(message))
        }
    }


}
