package com.example.hybridconnect.presentation.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridconnect.domain.model.Agent
import com.example.hybridconnect.domain.repository.AuthRepository
import com.example.hybridconnect.domain.usecase.UpdateAgentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val updateAgentUseCase: UpdateAgentUseCase,
) : ViewModel() {
    private val agent: StateFlow<Agent?> = authRepository.agent

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    private val _firstName = MutableStateFlow(agent.value?.firstName ?: "")
    val firstName: StateFlow<String> = _firstName

    private val _lastName = MutableStateFlow(agent.value?.lastName ?: "")
    val lastName: StateFlow<String> = _lastName

    private val _phoneNumber = MutableStateFlow(agent.value?.phoneNumber ?: "")
    val phoneNumber: StateFlow<String> = _phoneNumber

    private val _email = MutableStateFlow(agent.value?.email ?: "")
    val email: StateFlow<String> = _email

    // errors
    private val _firstNameError = MutableStateFlow<String?>(null)
    val firstNameError: StateFlow<String?> = _firstNameError

    private val _lastNameError = MutableStateFlow<String?>(null)
    val lastNameError: StateFlow<String?> = _lastNameError

    private val _phoneNumberError = MutableStateFlow<String?>(null)
    val phoneNumberError: StateFlow<String?> = _phoneNumberError

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError

    init {
        loadProfileInfo()
    }

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

    fun updateProfile() {
        if (!validateInputs()) return
        val agentId = agent.value?.id ?: return
        val pin = agent.value?.pin ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val updatedAgent = Agent(
                id = agentId,
                firstName = firstName.value,
                lastName = lastName.value,
                phoneNumber = phoneNumber.value,
                email = email.value,
                pin = pin,
            )
            try {
                _isLoading.value = true
                updateAgentUseCase(updatedAgent)
                _snackbarMessage.value = "Profile updated successfully"
            } catch (e: Exception) {
                _snackbarMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadProfileInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                authRepository.fetchAgent()
            } catch (e: Exception) {
                _snackbarMessage.value = e.message
            }
        }
    }

    private fun validateInputs(): Boolean {
        clearErrors()
        var isValid = true

        if (firstName.value.isEmpty()) {
            _firstNameError.value = "Full name is required"
            isValid = false
        } else if (firstName.value.length < 3) {
            _firstNameError.value = "Full name must be at least 3 characters"
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
}