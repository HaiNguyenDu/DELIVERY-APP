package com.example.grabapp.ui.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.grabapp.base.BaseViewModel
import com.example.grabapp.data.local.user.UserDao
import com.example.grabapp.data.repository.OderRepositoryImpl
import com.example.grabapp.data.repository.UserRepositoryImpl
import com.example.grabapp.domain.model.order.OrderItem
import com.example.grabapp.domain.model.user.User
import com.example.grabapp.domain.use_case.GetUserUseCase
import com.example.grabapp.domain.use_case.InsertUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.maplibre.android.style.light.Position

class MainViewModel(userDao: UserDao, application: Application) : BaseViewModel(application) {

    private val repository = UserRepositoryImpl(userDao)
    private val orderRepository = OderRepositoryImpl()
    private val getUserUseCase = GetUserUseCase(repository)
    private val updateUserUseCase = InsertUserUseCase(repository)
    private val _listOrder = MutableStateFlow<List<OrderItem>>(emptyList())
    val listOrder: StateFlow<List<OrderItem>> = _listOrder
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    init {
        loadUser()
        loadListOrder()
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

    fun loadListOrder() {
        viewModelScope.launch {
            showLoading()
            val result = orderRepository.getListOrder(1, 50, "createdAt,desc")
            result.onSuccess {
                _listOrder.value = it
                hideLoading()
            }
            result.onFailure {
                _listOrder.value = emptyList()
                hideLoading()
            }
        }
    }

    fun getDetailOrder(position: Int): OrderItem {
        return _listOrder.value[position]
    }
}
