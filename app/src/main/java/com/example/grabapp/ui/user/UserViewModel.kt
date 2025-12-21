package com.example.grabapp.ui.user

import android.app.Application
import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.grabapp.base.BaseViewModel
import com.example.grabapp.data.repository.FileRepositoryImpl
import com.example.grabapp.data.repository.UserRepositoryImpl
import com.example.grabapp.domain.model.user.User
import com.example.grabapp.domain.model.user.toUser
import com.example.grabapp.extention.toBase64
import com.example.grabapp.utils.JwtUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class UserViewModel(application: Application) : BaseViewModel(application) {
    val userRepository = UserRepositoryImpl()
    val fileRepository = FileRepositoryImpl()
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user
    private val _img = MutableStateFlow<String?>(null)
    val img: StateFlow<String?> = _img

    val _isUpdate = MutableSharedFlow<Boolean>()
    val isUpdate: SharedFlow<Boolean> = _isUpdate
    init {
        loadUser()
    }

    fun setImageUrl(uriString: String) {
        _img.value = uriString
    }

    fun updateUser(
        context: Context,
        phone: String,
        fullName: String,
        date: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            img.value?.let { uriString ->
                val file = uriToFile(application, uriString.toUri(), "avatar.jpg")
                val url = fileRepository.upload(_user.value?.id?:"",file)
                _user.value = _user.value?.copy(
                    avatarUrl = url?.url,
                    phone = phone,
                    fullName = fullName,
                    date = date
                )

                _user.value?.let {
                    val result = userRepository.updateUser(it)
                    result.onSuccess {
                        loadUser()
                        _isUpdate.emit(true)
                    }
                }
                return@launch
            }

            _user.value = _user.value?.copy(
                phone = phone,
                fullName = fullName,
                date = date
            )

            _user.value?.let {
                val result = userRepository.updateUser(it)
                result.onSuccess {
                    loadUser()
                    _isUpdate.emit(true)
                }
            }
        }
    }

    fun uriToFile(context: Context, uri: Uri, fileName: String): File {
        val inputStream = context.contentResolver.openInputStream(uri)!!
        val file = File(context.cacheDir, fileName)
        file.createNewFile()
        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }
        return file
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
}