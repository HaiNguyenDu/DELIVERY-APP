package com.example.grabapp.ui.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.grabapp.base.BaseViewModel
import com.example.grabapp.data.local.user.UserDao
import com.example.grabapp.data.repository.UserRepositoryImpl
import com.example.grabapp.domain.model.user.User
import com.example.grabapp.domain.use_case.GetUserUseCase
import com.example.grabapp.domain.use_case.InsertUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(userDao: UserDao, application: Application) : BaseViewModel(application) {

    private val repository = UserRepositoryImpl(userDao)
    private val getUserUseCase = GetUserUseCase(repository)
    private val updateUserUseCase = InsertUserUseCase(repository)

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    init {
        loadUser()
    }

    fun loadUser() = viewModelScope.launch {
        getUserUseCase().collect {
            _user.value = it
        }
    }

    fun insertUser(user: User) = viewModelScope.launch {
        updateUserUseCase(user)
        _user.value = user
    }
}
