package com.example.hybridconnect.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridconnect.domain.enums.AppState
import com.example.hybridconnect.domain.model.Agent
import com.example.hybridconnect.domain.model.ConnectedApp
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.AuthRepository
import com.example.hybridconnect.domain.repository.ConnectedAppRepository
import com.example.hybridconnect.domain.repository.SettingsRepository
import com.example.hybridconnect.domain.repository.TransactionRepository
import com.example.hybridconnect.domain.services.SocketService
import com.example.hybridconnect.domain.services.interfaces.AppControl
import com.example.hybridconnect.domain.usecase.LogoutUserUseCase
import com.example.hybridconnect.domain.usecase.RetryUnforwardedTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
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
    private val appControl: AppControl,
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
    private val logoutUserUseCase: LogoutUserUseCase,
    private val connectedAppRepository: ConnectedAppRepository,
    private val socketService: SocketService,
    private val transactionRepository: TransactionRepository,
    private val retryUnforwardedTransactionsUseCase: RetryUnforwardedTransactionsUseCase,
) : ViewModel() {
    val appState: StateFlow<AppState> = appControl.appState

    val connectedApps: StateFlow<List<ConnectedApp>> = connectedAppRepository.getConnectedApps()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val isAppActive: StateFlow<Boolean> = settingsRepository.isAppActive

    private val agent: StateFlow<Agent?> = authRepository.agent

    val agentFirstName: StateFlow<String?> = agent
        .map { it?.firstName }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    private val _greetings = MutableStateFlow("Hello")
    val greetings: StateFlow<String> = _greetings.asStateFlow()

    // logout
    private val _logoutSuccess = MutableStateFlow(false)
    val logoutSuccess: StateFlow<Boolean> = _logoutSuccess

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    val isConnected: StateFlow<Boolean> = socketService.isConnected

    val transactionQueue: StateFlow<List<Transaction>> = transactionRepository.transactionQueueFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    private val _connectedOffersCount = MutableStateFlow<Map<String, Int>>(emptyMap())
    val connectedOffersCount: StateFlow<Map<String, Int>> = _connectedOffersCount.asStateFlow()

    init {
        loadAgent()
        createGreetings()
        startGreetingTimer()
        loadConnectedOffersCount()
        observeConnectedApps()
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

    private fun loadConnectedOffersCount() {
        viewModelScope.launch {
            try {
                connectedAppRepository.getAllConnectedOffersCount().collect { offersCountMap ->
                    _connectedOffersCount.value = offersCountMap
                }
            } catch (e: Exception) {
                _snackbarMessage.value = e.message
            }
        }
    }

    private fun observeConnectedApps() {
        viewModelScope.launch {
            connectedAppRepository.getConnectedApps().collect { apps ->
                if (apps.none { it.isOnline }) {
                    Log.d(TAG, "No app was found online. Clearing queue")
                    transactionRepository.transactionQueue.clear()
                }
            }
        }
    }

    fun toggleAppState() {
        viewModelScope.launch {
            when (appState.value) {
                AppState.STATE_RUNNING -> {
                    appControl.pauseApp()
                    _snackbarMessage.value = "App has been paused"
                }

                AppState.STATE_PAUSED -> {
                    appControl.resumeApp()
                    _snackbarMessage.value = "App resumed successfully"
                    retryUnforwardedTransactionsUseCase()
                }

                AppState.STATE_STOPPED -> {
                    appControl.startApp()
                    retryUnforwardedTransactionsUseCase()
                    _snackbarMessage.value = "App started successfully"
                }
            }
        }
    }

    fun toggleOnlineState() {
        viewModelScope.launch {
            if (isConnected.value) {
                socketService.disconnect()
            } else {
                socketService.connect()
            }
        }
    }

    fun stopApp() {
        viewModelScope.launch {
            try {
                appControl.stopApp()
                _snackbarMessage.value = "App stopped successfully"
            } catch (e: Exception) {
                _snackbarMessage.value = e.message
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

    private fun startGreetingTimer() {
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
}
