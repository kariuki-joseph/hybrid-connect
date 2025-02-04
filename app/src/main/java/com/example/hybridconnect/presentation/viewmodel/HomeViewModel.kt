package com.example.hybridconnect.presentation.viewmodel

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.model.Agent
import com.example.hybridconnect.domain.model.ConnectedApp
import com.example.hybridconnect.domain.repository.AuthRepository
import com.example.hybridconnect.domain.repository.ConnectedAppRepository
import com.example.hybridconnect.domain.repository.PrefsRepository
import com.example.hybridconnect.domain.services.SmsProcessingService
import com.example.hybridconnect.domain.usecase.LogoutUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepository: AuthRepository,
    private val prefsRepository: PrefsRepository,
    private val logoutUserUseCase: LogoutUserUseCase,
    private val connectedAppRepository: ConnectedAppRepository
) : ViewModel() {
    private val _connectedApps = MutableStateFlow<List<ConnectedApp>>(emptyList())
    val connectedApps: StateFlow<List<ConnectedApp>> = _connectedApps.asStateFlow()

    val isAppActive: StateFlow<Boolean> = prefsRepository.isAppActive

    private val agent: StateFlow<Agent?> = authRepository.agent

    private var countdownJob: Job? = null

    val agentFirstName: StateFlow<String?> = agent.map { agent ->
        agent?.firstName
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    private val _greetings = MutableStateFlow("Hello")
    val greetings: StateFlow<String> = _greetings.asStateFlow()

    // logout
    private val _logoutSuccess = MutableStateFlow(false)
    val logoutSuccess: StateFlow<Boolean> = _logoutSuccess

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    init {
        loadAgent()
        loadConnectedApps()
        createGreetings()
        startGreetingTimer()
    }

    private fun stopCountdownTimer() {
        countdownJob?.cancel()
        countdownJob = null
    }

    private fun loadAgent() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                authRepository.fetchAgent()
            } catch (e: Exception) {
                _snackbarMessage.value = e.message.toString()
            }
        }
    }

    private fun loadConnectedApps(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                connectedAppRepository.getConnectedApps().collect { apps ->
                    _connectedApps.value = apps
                }
            } catch (e: Exception) {
                _snackbarMessage.value = e.message.toString()
            }

        }
    }

    fun toggleAppState() {
        viewModelScope.launch {
            val newState = !prefsRepository.isAppActive.value
            prefsRepository.saveSetting(AppSetting.IS_USSD_PROCESSING, false.toString())
            if (newState) {
                prefsRepository.setAppActive(true)
                startService()
                _snackbarMessage.value = "Requests processing started successfully"
            } else {
                prefsRepository.setAppActive(false)
                stopService()
                _snackbarMessage.value = "Requests processing has been paused"
            }
        }
    }

    private fun createGreetings() {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when (currentHour) {
            in 0..11 -> "Good Morning"
            in 12..14 -> "Good Afternoon"
            in 15..22 -> "Good Evening"
            else -> "Good Night"
        }
        _greetings.value = greeting
    }

    private fun startGreetingTimer(){
        viewModelScope.launch {
            while (true) {
                delay(1000 * 60)
                createGreetings()
            }
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            try {
                logoutUserUseCase()
                _logoutSuccess.value = true
            } catch (e: Exception) {
                _logoutSuccess.value = false
                _snackbarMessage.value = e.message.toString()
            }
        }
    }

    fun resetSnackbarMessage() {
        _snackbarMessage.value = null
    }

    private fun startService() {
        val serviceIntent = Intent(context, SmsProcessingService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    private fun stopService() {
        val serviceIntent = Intent(context, SmsProcessingService::class.java)
        context.stopService(serviceIntent)
    }
}
