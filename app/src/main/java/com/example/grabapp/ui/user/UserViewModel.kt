package com.example.grabapp.ui.user

import android.app.Application
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.grabapp.base.BaseViewModel
import com.example.grabapp.data.repository.FileRepositoryImpl
import com.example.grabapp.data.repository.UserRepositoryImpl
import com.example.grabapp.domain.model.user.User
import com.example.grabapp.domain.model.user.toUser
import com.example.grabapp.utils.JwtUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : BaseViewModel(application) {
    val userRepository = UserRepositoryImpl()
    val fileRepository = FileRepositoryImpl()
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    init {
        loadUser()
    }

    fun updateUser() {
        viewModelScope.launch {
            userRepository.updateUser(_user.value!!)
        }
    }

    fun getUser(): User? {
        return _user.value
    }

    private fun loadUser() {
        viewModelScope.launch {
            val userResponse =
                userRepository.getUserByIdFromSever(JwtUtils.getUserId(application) ?: "")
            userResponse?.let {
                _user.value = it.toUser()
            }
        }
    }

    private fun upLoadFile(){
        viewModelScope.launch {

        }
    }
}