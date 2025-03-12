package com.example.hybridconnect.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridconnect.domain.repository.AuthRepository
import com.example.hybridconnect.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PinSetupViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val pinDelay = 500L

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isConfirming = MutableStateFlow(false)
    val isConfirming: StateFlow<Boolean> = _isConfirming

    private val _isPinError = MutableStateFlow(false)
    val isPinError: StateFlow<Boolean> = _isPinError

    private val _pinSetupSuccess = MutableStateFlow(false)
    val pinSetupSuccess: StateFlow<Boolean> = _pinSetupSuccess

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    private val _pin = MutableStateFlow("")
    val pin: StateFlow<String> = _pin

    private val _confirmPin = MutableStateFlow("")
    val confirmPin: StateFlow<String> = _confirmPin

    fun onDeletePin() {
        if (!isConfirming.value) {
            _pin.value = _pin.value.dropLast(1)
            return
        }
        _confirmPin.value = _confirmPin.value.dropLast(1)
    }

    fun onNumberClick(number: String) {
        if (!isConfirming.value) {
            if (_pin.value.length < 4) {
                _pin.value += number
            }
            if (_pin.value.length == 4) {
                viewModelScope.launch {
                    delay(pinDelay)
                    _isConfirming.value = true
                }
                return
            }
            return
        }

        // confirming PIN
        if (_confirmPin.value.length < 4) {
            _confirmPin.value += number
        }

        if (_confirmPin.value.length == 4) {
            if (_pin.value != _confirmPin.value) {
                _isPinError.value = true
                viewModelScope.launch {
                    delay(pinDelay+2000)
                    clearPin()
                    _isConfirming.value = false
                    _isPinError.value = false
                }
                return
            }

            viewModelScope.launch(Dispatchers.IO) {
                attemptPinSetup()
            }
        }
    }

    private suspend fun attemptPinSetup() {
        val pin = _pin.value
        try {
            _isLoading.value = true
            authRepository.updateAgentPin(pin)
            _snackbarMessage.value = "PIN setup successful"
            _pinSetupSuccess.value = true
        } catch (e: Exception) {
            clearPin()
            _snackbarMessage.value = e.message
            _pinSetupSuccess.value = false
        } finally {
            _isLoading.value = false
        }
    }

    fun resetSnackbarMessage() {
        _snackbarMessage.value = null
    }

    private fun clearPin() {
        _pin.value = ""
        _confirmPin.value = ""
    }
}