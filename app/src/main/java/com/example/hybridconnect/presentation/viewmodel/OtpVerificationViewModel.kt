package com.example.hybridconnect.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridconnect.domain.repository.AuthRepository
import com.example.hybridconnect.domain.usecase.VerifyOtpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpVerificationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val verifyOtpUseCase: VerifyOtpUseCase,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var email = ""

    private val _otp = MutableStateFlow(mutableListOf("", "", "", "", "", ""))
    val otp: StateFlow<MutableList<String>> = _otp

    private val _timerSeconds = MutableStateFlow(120)
    val timerSeconds: StateFlow<Int> = _timerSeconds

    private val _otpVerificationSuccess = MutableStateFlow(false)
    val otpVerificationSuccess: StateFlow<Boolean> = _otpVerificationSuccess

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage


    init {
        updateTimer()
    }

    fun onOtpChanged(index: Int, digit: String) {
        _otp.value = _otp.value.toMutableList().apply {
            if (digit.isEmpty()) {
                // Handle backspace: Clear the field at the given index
                this[index] = ""
            } else {
                // Handle adding the digit
                this[index] = digit
            }
        }
    }


    fun setEmail(email: String) {
        this.email = email
    }

    private fun updateTimer() {
        viewModelScope.launch(Dispatchers.IO) {
            while (_timerSeconds.value > 0) {
                delay(1000)
                _timerSeconds.value -= 1
            }
        }
    }

    fun verifyOtp() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val otp = _otp.value.joinToString("")
                _isLoading.value = true
                verifyOtpUseCase(otp)
                _otpVerificationSuccess.value = true
            } catch (e: Exception) {
                _snackbarMessage.value = e.message
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resendOtp() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true
                authRepository.sendOtp(email)
                _isLoading.value = false
                _timerSeconds.value = 120
                updateTimer()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun resetSnackbarMessage() {
        _snackbarMessage.value = null
    }
}