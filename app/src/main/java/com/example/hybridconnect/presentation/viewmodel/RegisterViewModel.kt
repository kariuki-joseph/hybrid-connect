package com.example.hybridconnect.presentation.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridconnect.domain.model.Agent
import com.example.hybridconnect.domain.repository.AuthRepository
import com.example.hybridconnect.domain.usecase.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val registerUserUseCase: RegisterUserUseCase,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    private val _firstName = MutableStateFlow("")
    val firstName: StateFlow<String> = _firstName

    private val _lastName = MutableStateFlow("")
    val lastName: StateFlow<String> = _lastName

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _pin = MutableStateFlow("1234")

    private val _firstNameError = MutableStateFlow<String?>(null)
    val firstNameError: StateFlow<String?> = _firstNameError

    private val _lastNameError = MutableStateFlow<String?>(null)
    val lastNameError: StateFlow<String?> = _lastNameError

    private val _phoneNumberError = MutableStateFlow<String?>(null)
    val phoneNumberError: StateFlow<String?> = _phoneNumberError

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError


    fun onFirstNameChanged(firstName: String) {
        if (_firstNameError.value != null) _firstNameError.value = null
        _firstName.value = firstName
    }

    fun onLastNameChanged(lastName: String) {
        if (_lastNameError.value != null) _lastNameError.value = null
        _lastName.value = lastName
    }

    fun onPhoneNumberChanged(phoneNumber: String) {
        if (_phoneNumberError.value != null) _phoneNumberError.value = null
        _phoneNumber.value = phoneNumber
    }

    fun onEmailChanged(email: String) {
        if (_emailError.value != null) _emailError.value = null
        _email.value = email
    }

    fun register() {
        if (!validateInputs()) return
        clearErrors()
        val firstName = firstName.value
        val lastName = lastName.value
        val phone = phoneNumber.value
        val email = email.value
        val pin = _pin.value

        viewModelScope.launch(Dispatchers.IO) {
            val agent = Agent(
                id = UUID.randomUUID(),
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phone,
                email = email,
                pin = pin
            )
            try {
                _isLoading.value = true
                registerUserUseCase(agent)
                _isLoading.value = false
                _registerSuccess.value = true
            } catch (e: Exception) {
                _isLoading.value = false
                _snackbarMessage.value = e.message
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (firstName.value.isEmpty()) {
            _firstNameError.value = "First name is required"
            isValid = false
        } else if (firstName.value.length < 3) {
            _firstNameError.value = "First name must be at least 2 characters"
            isValid = false
        }

        if (lastName.value.isEmpty()) {
            _lastNameError.value = "Last name is required"
            isValid = false
        } else if (lastName.value.length < 3) {
            _lastNameError.value = "Last name must be at least 2 characters"
            isValid = false
        }

        if (phoneNumber.value.isEmpty()) {
            _phoneNumberError.value = "Phone number is required"
            isValid = false
        } else if (!phoneNumber.value.matches(Regex("^[0-9]{10}$"))) {
            _phoneNumberError.value = "Phone number must be 10 digits"
            isValid = false
        }

        if (email.value.isEmpty()) {
            _emailError.value = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
            _emailError.value = "Invalid email format"
            isValid = false
        }
        return isValid
    }

    private fun clearErrors() {
        _firstNameError.value = null
        _phoneNumberError.value = null
        _emailError.value = null
    }

    fun resetSnackbarMessage() {
        _snackbarMessage.value = null
    }

    fun registerWithGoogle() {

    }
}