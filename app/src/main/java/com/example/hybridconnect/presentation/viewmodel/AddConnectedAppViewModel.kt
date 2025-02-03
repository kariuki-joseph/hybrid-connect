package com.example.hybridconnect.presentation.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridconnect.domain.model.Agent
import com.example.hybridconnect.domain.repository.AuthRepository
import com.example.hybridconnect.domain.usecase.LoginCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddConnectedAppViewModel @Inject constructor(
    private val loginCoordinator: LoginCoordinator,
    private val authRepository: AuthRepository
) : ViewModel() {
    val agent: StateFlow<Agent?> = authRepository.agent

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _pin = MutableStateFlow("")
    val pin: StateFlow<String> = _pin

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun onDeletePin() {
        _pin.value = _pin.value.dropLast(1)
    }

    fun onEmailChanged(email: String) {
        if (_errorMessage.value != null) _errorMessage.value = null
        _email.value = email
    }

    fun validateEmail(): Boolean {
        if (_email.value.isEmpty()) {
            _errorMessage.value = "You have not entered your email"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(_email.value).matches()) {
            _errorMessage.value = "Invalid email address"
            return false
        }
        return true
    }

    fun onNumberClick(number: String) {
        if (errorMessage.value != null) return

        if (_pin.value.length < 4) {
            _pin.value += number
        }

        if (_pin.value.length == 4) {
            viewModelScope.launch(Dispatchers.IO) {
                attemptLogin()
            }
        }
    }

    private suspend fun attemptLogin() {
        val pin = _pin.value
        try {
            _isLoading.value = true
            loginCoordinator(_email.value, pin)
            _loginSuccess.value = true

        } catch (e: Exception) {
            _errorMessage.value = e.message
            _loginSuccess.value = false
            resetErrorMessage()
        } finally {
            _isLoading.value = false
        }
    }

    private fun resetErrorMessage() {
        viewModelScope.launch {
            delay(2000)
            _pin.value = ""
            _errorMessage.value = null
        }
    }
}