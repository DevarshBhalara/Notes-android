package com.example.notes_android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes_android.ui.repository.CreateUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateUserViewModel @Inject constructor(
    private val createUserRepository: CreateUserRepository
): ViewModel() {

    private val _emailValidation = MutableStateFlow<String>("")
    var emailValidation = _emailValidation.asStateFlow()

    private val _passwordValidation = MutableStateFlow<String>("")
    var passwordValidation = _passwordValidation.asStateFlow()

    private val _confPasswordValidation = MutableStateFlow<String>("")
    var confPasswordValidation = _confPasswordValidation.asStateFlow()

    private val _isSuccess = MutableStateFlow<Boolean>(false)
    var isSuccess = _isSuccess.asStateFlow()

    private val _isSuccessString = MutableStateFlow<String>("")
    var isSuccessString = _isSuccessString.asStateFlow()


    fun validation(email: String, password: String, confPassword: String) {
        if (email.isEmpty()) {
            _emailValidation.value = "Please Enter Email"
        }

        if (password.isEmpty()) {
            _passwordValidation.value = "Please Enter Password"
        }

        if (confPassword.isEmpty()) {
            _confPasswordValidation.value = "Please Re-Enter Password"
        }

        if(email.isNotEmpty() && password.isNotEmpty() && confPassword.isNotEmpty()) {
            createUser(email, password)
        }
    }

    fun createUser(email: String, password: String) {
        viewModelScope.launch {
            createUserRepository.createUserWithEmailPassword(email, password).collectLatest { result ->
                val isSuccess = result.keys.first()
                val message = result.values.first()
                _isSuccessString.emit(message)
                _isSuccess.emit(isSuccess)
            }
        }
    }

    fun createUserWithGoogle() {

    }
}