package com.example.hybridconnect.presentation.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridconnect.domain.exception.InvalidEmailException
import com.example.hybridconnect.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _sendSuccess = MutableStateFlow(false)
    val sendSuccess: StateFlow<Boolean> = _sendSuccess.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    fun onEmailChanged(email: String) {
        if (!_emailError.value.isNullOrEmpty()) {
            _emailError.value = null
        }
        _email.value = email
    }

    fun sendResetPin() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                validateEmail(_email.value)
                _isLoading.value = true
                authRepository.sendOtp(_email.value)
                _sendSuccess.value = true
            } catch (e: InvalidEmailException) {
                _emailError.value = e.message.toString()
            } catch (e: Exception) {
                _sendSuccess.value = false
                _isLoading.value = false
                _snackbarMessage.value = e.message.toString()
            }
        }
    }

    fun resetSnackbarMessage() {
        _snackbarMessage.value = null
    }

    private fun validateEmail(email: String) {
        if (email.isEmpty()) {
            throw InvalidEmailException("You have not entered your email")
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            throw InvalidEmailException("Invalid email address")
        }
    }
}