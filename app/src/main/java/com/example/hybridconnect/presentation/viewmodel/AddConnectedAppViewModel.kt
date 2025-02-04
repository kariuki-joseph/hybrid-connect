package com.example.hybridconnect.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridconnect.domain.model.ConnectedApp
import com.example.hybridconnect.domain.repository.ConnectedAppRepository
import com.example.hybridconnect.domain.services.SocketService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddConnectedAppViewModel @Inject constructor(
    private val connectedAppRepository: ConnectedAppRepository,
    private val socketService: SocketService,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _connectSuccess = MutableStateFlow(false)
    val connectSuccess: StateFlow<Boolean> = _connectSuccess

    private val _connectId = MutableStateFlow("")
    val connectId: StateFlow<String> = _connectId.asStateFlow()

    private val _appName = MutableStateFlow("")
    val appName: StateFlow<String> = _appName.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun onConnectIdChanged(connectId: String) {
        _connectId.value = connectId
    }

    fun onAppNameChanged(appName: String){
        _appName.value = appName
    }


    fun attemptConnect() {
        val connectId = "BHC-${_connectId.value}"
        val appName = _appName.value

        viewModelScope.launch {
            try {
                _isLoading.value = true
                validateConnectId(connectId)
                validateAppName(appName)
                val canConnect = connectedAppRepository.checkCanConnectToApp(connectId)
                if (!canConnect) {
                    throw Exception("You are only allowed to connect to your apps. Please confirm your account is active in the device with this ConnectID")
                }
                val connectedApp = ConnectedApp(
                    connectId, isOnline = false,
                    appName = appName,
                )
                connectedAppRepository.addConnectedApp(connectedApp)
                _connectSuccess.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _connectSuccess.value = false
                resetErrorMessage()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun validateConnectId(connectId: String) {
        val id = connectId.split("-")[1]
        if(id.length < 5){
            throw Exception("ConnectID is too short")
        }
        if(id.length > 5){
            throw Exception("Invalid ConnectID")
        }
    }

    private fun validateAppName(appName: String) {
        if(appName.length < 3){
            throw Exception("App name should be at least 3 characters long")
        }
    }

    fun resetErrorMessage() {
        viewModelScope.launch {
            delay(2000)
            _errorMessage.value = null
        }
    }
}