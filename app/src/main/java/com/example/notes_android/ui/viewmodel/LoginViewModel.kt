package com.example.notes_android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes_android.ui.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
): ViewModel() {

    private val _isSuccess = MutableStateFlow<Boolean>(false)
    var isSuccess = _isSuccess.asStateFlow()

    private val _message = MutableStateFlow<String>("")
    var message = _message.asStateFlow()

    fun createUser(email: String, password: String) {
        viewModelScope.launch {
            loginRepository.loginUser(email, password).collectLatest { result ->
                val isSuccess = result.keys.first()
                val message = result.values.first()
                _message.emit(message)
                _isSuccess.emit(isSuccess)
            }
        }

    }
}