package com.example.hybridconnect.presentation.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.model.Agent
import com.example.hybridconnect.domain.model.ConnectedApp
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.AuthRepository
import com.example.hybridconnect.domain.repository.ConnectedAppRepository
import com.example.hybridconnect.domain.repository.SettingsRepository
import com.example.hybridconnect.domain.repository.TransactionRepository
import com.example.hybridconnect.domain.services.SmsProcessingService
import com.example.hybridconnect.domain.services.SmsProcessor
import com.example.hybridconnect.domain.services.SocketService
import com.example.hybridconnect.domain.usecase.LogoutUserUseCase
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
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
    private val logoutUserUseCase: LogoutUserUseCase,
    private val connectedAppRepository: ConnectedAppRepository,
    private val socketService: SocketService,
    private val transactionRepository: TransactionRepository,
    private val smsProcessor: SmsProcessor,
) : ViewModel() {
    private val _connectedApps = MutableStateFlow<List<ConnectedApp>>(emptyList())
    val connectedApps: StateFlow<List<ConnectedApp>> = _connectedApps.asStateFlow()

    val isAppActive: StateFlow<Boolean> = settingsRepository.isAppActive

    private val agent: StateFlow<Agent?> = authRepository.agent

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

    val isConnected: StateFlow<Boolean> = socketService.isConnected

    val transactionQueue: StateFlow<List<Transaction>> = transactionRepository.transactionQueueFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    private val _connectedOffersCount = MutableStateFlow<Map<String, Int>>(emptyMap())
    val connectedOffersCount: StateFlow<Map<String, Int>> = _connectedOffersCount.asStateFlow()

    private var count = 0;

    init {
        loadAgent()
        loadConnectedApps()
        createGreetings()
        startGreetingTimer()
        loadConnectedOffersCount()
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

    private fun loadConnectedApps() {
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

    fun toggleAppState() {
        viewModelScope.launch {
            val newState = !settingsRepository.isAppActive.value
            settingsRepository.saveSetting(AppSetting.IS_USSD_PROCESSING, false.toString())
            if (newState) {
                settingsRepository.setAppActive(true)
                startService()
                _snackbarMessage.value = "Requests processing started successfully"
            } else {
                settingsRepository.setAppActive(false)
                stopService()
                _snackbarMessage.value = "Requests processing has been paused"
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

    private fun startService() {
        val serviceIntent = Intent(context, SmsProcessingService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    private fun stopService() {
        val serviceIntent = Intent(context, SmsProcessingService::class.java)
        context.stopService(serviceIntent)
    }

    private fun sendMessage(message: String) {
        Log.d(TAG, "Trying to send message: $message")
        smsProcessor.processMessage(message, "MPESA", 1)
    }

    fun testButtonClicked(amount: String = "5") {
        val transactionCode = "TCB49LSF1K${++count}"
        val message =
            "$transactionCode Confirmed.You have received Ksh$amount.00 from Joseph  Kariuki 0114662464 on 11/3/25 at 1:21 PM  New M-PESA balance is Ksh9.73. Earn interest daily on Ziidi MMF,Dial *334#"
        sendMessage(message)
    }
}
