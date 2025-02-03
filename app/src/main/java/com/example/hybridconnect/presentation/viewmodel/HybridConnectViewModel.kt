package com.example.hybridconnect.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.enums.SocketEvent
import com.example.hybridconnect.domain.repository.PrefsRepository
import com.example.hybridconnect.domain.services.SocketService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HybridConnectViewModel"

@HiltViewModel
class HybridConnectViewModel @Inject constructor(
    private val socketService: SocketService,
    private val prefsRepository: PrefsRepository,
) : ViewModel() {
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _connectId = MutableStateFlow<String?>(null)
    val connectId: StateFlow<String?> = _connectId.asStateFlow()

    val isOnline: StateFlow<Boolean> = socketService.isConnected

    private val _connectedCount = MutableStateFlow(0)
    val connectedCount: StateFlow<Int> = _connectedCount.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _connectionStatusProgress = MutableStateFlow(false)
    val connectionStatusProgress: StateFlow<Boolean> = _connectionStatusProgress.asStateFlow()

    init {
        socketConnectionStatusListeners()
        fetchConnectId()
    }

    private fun socketConnectionStatusListeners() {
        socketService.on(SocketEvent.EVENT_CONNECT_ERROR.name) { message ->
            Log.d(TAG, "Error connecting to socket: ${message[0]}")
            _connectionStatusProgress.value = false
            _errorMessage.value = message[0].toString()
        }

        viewModelScope.launch {
            socketService.isConnected.collect {
                _connectionStatusProgress.value = false
            }
        }
    }

    private fun fetchConnectId() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val connectId = prefsRepository.getSetting(AppSetting.APP_CONNECT_ID)
                if (connectId.isNotEmpty()) {
                    _connectId.value = connectId
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleConnectionStatus(prevStatus: Boolean) {
        viewModelScope.launch {
            try {
                _connectionStatusProgress.value = true
                if (prevStatus) {
                    Log.d(TAG, "Attempting to disconnect...")
                    socketService.disconnect()
                } else {
                    Log.d(TAG, "Attempting to connect...")
                    socketService.connect()
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }


    fun resetErrorMessage() {
        _errorMessage.value = null
    }

    fun resetSuccessMessage() {
        _successMessage.value = null
    }

}