package com.example.hybridconnect.presentation.viewmodel

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.enums.SubscriptionType
import com.example.hybridconnect.domain.enums.TransactionType
import com.example.hybridconnect.domain.model.Agent
import com.example.hybridconnect.domain.model.AgentCommission
import com.example.hybridconnect.domain.model.SubscriptionPlan
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.AuthRepository
import com.example.hybridconnect.domain.repository.PrefsRepository
import com.example.hybridconnect.domain.repository.SubscriptionPlanRepository
import com.example.hybridconnect.domain.services.SmsProcessingService
import com.example.hybridconnect.domain.usecase.DecrementCustomerBalanceUseCase
import com.example.hybridconnect.domain.usecase.DialUssdUseCase
import com.example.hybridconnect.domain.usecase.FormatUssdUseCase
import com.example.hybridconnect.domain.usecase.GetCommissionForDatesUseCase
import com.example.hybridconnect.domain.usecase.LogoutUserUseCase
import com.example.hybridconnect.domain.usecase.ObserveTransactionsUseCase
import com.example.hybridconnect.domain.usecase.RetryTransactionUseCase
import com.example.hybridconnect.domain.usecase.UpdateTransactionStatusUseCase
import com.example.hybridconnect.domain.utils.todayRange
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
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import javax.inject.Inject

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val observeTransactionsUseCase: ObserveTransactionsUseCase,
    private val authRepository: AuthRepository,
    private val prefsRepository: PrefsRepository,
    private val dialUssdUseCase: DialUssdUseCase,
    private val updateTransactionStatusUseCase: UpdateTransactionStatusUseCase,
    private val decrementCustomerBalanceUseCase: DecrementCustomerBalanceUseCase,
    private val formatUssdUseCase: FormatUssdUseCase,
    private val subscriptionPlanRepository: SubscriptionPlanRepository,
    private val getCommissionForDatesUseCase: GetCommissionForDatesUseCase,
    private val logoutUserUseCase: LogoutUserUseCase,
    private val retryTransactionUseCase: RetryTransactionUseCase,
) : ViewModel() {
    val isAppActive: StateFlow<Boolean> = prefsRepository.isAppActive

    private val agent: StateFlow<Agent?> = authRepository.agent

    private val activePlans: StateFlow<List<SubscriptionPlan>> =
        subscriptionPlanRepository.activePlansFlow

    private val _activeSubscriptionType = MutableStateFlow<SubscriptionType?>(null)
    val activeSubscriptionType: StateFlow<SubscriptionType?> = _activeSubscriptionType

    private val _subscriptionLimit = MutableStateFlow(0L)
    val subscriptionLimit: StateFlow<Long> = _subscriptionLimit.asStateFlow()

    private var countdownJob: Job? = null

    val agentFirstName: StateFlow<String?> = agent.map { agent ->
        agent?.firstName
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    private val _greetings = MutableStateFlow("Hello")
    val greetings: StateFlow<String> = _greetings.asStateFlow()

    val transactions: StateFlow<List<Transaction>> = observeTransactionsUseCase.transactions
        .map { list ->
            list.filter {
                it.rescheduleInfo?.parentTransactionId == null && it.type != TransactionType.SUBSCRIPTION_RENEWAL && it.status != TransactionStatus.SCHEDULED
            }
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Counts for different statuses
    val successCount: StateFlow<Int> = transactions.map { list ->
        list.count { it.status == TransactionStatus.SUCCESS && it.time in todayRange() }
    }.stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = 0)

    val failedCount: StateFlow<Int> = transactions.map { list ->
        list.count { it.status == TransactionStatus.FAILED && it.time in todayRange() }
    }.stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = 0)

    val agentCommissions: StateFlow<List<AgentCommission>> =
        getCommissionForDatesUseCase.agentCommissions

    // logout
    private val _logoutSuccess = MutableStateFlow(false)
    val logoutSuccess: StateFlow<Boolean> = _logoutSuccess

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    init {
        loadAgent()
        loadTransactions()
        createGreetings()
        startGreetingTimer()
        getAgentCommissions()
        observeActivePlans()
    }

    private fun loadTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            observeTransactionsUseCase()
        }
    }

    private fun observeActivePlans() {
        viewModelScope.launch {
            subscriptionPlanRepository.getActivePlans()
            activePlans.collect { plans ->
                // Prioritize UNLIMITED subscriptions if available
                val unlimitedPlan =
                    plans.firstOrNull { it.type == SubscriptionType.UNLIMITED && it.limit > System.currentTimeMillis() }
                if (unlimitedPlan != null) {
                    _activeSubscriptionType.value = SubscriptionType.UNLIMITED
                    startCountdownTimer(unlimitedPlan.limit)
                } else {
                    stopCountdownTimer()
                    // Fallback to LIMITED subscriptions
                    val limitedPlan =
                        plans.firstOrNull { it.type == SubscriptionType.LIMITED && it.limit > 0 }
                    if (limitedPlan != null) {
                        _activeSubscriptionType.value = SubscriptionType.LIMITED
                        _subscriptionLimit.value = limitedPlan.limit
                    } else {
                        _activeSubscriptionType.value = null
                        _subscriptionLimit.value = 0L
                    }
                }
            }
        }
    }

    private fun startCountdownTimer(limit: Long) {
        stopCountdownTimer()
        countdownJob = viewModelScope.launch {
            while (true) {
                val remainingTime = limit - System.currentTimeMillis()
                if (remainingTime <= 0) {
                    _subscriptionLimit.value = 0L
                    stopCountdownTimer()
                    break
                } else {
                    _subscriptionLimit.value = remainingTime
                }
                delay(1000L) // Update every second
            }
        }
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

    private fun getAgentCommissions() {
        viewModelScope.launch(Dispatchers.IO) {
            getCommissionForDatesUseCase(getCurrentWeekDates())
        }
    }

    private fun getCurrentWeekDates(): List<String> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val today = LocalDate.now()
        val startOfWeek = today.with(DayOfWeek.SUNDAY).let {
            if (today.dayOfWeek != DayOfWeek.SUNDAY) it.minusWeeks(1) else it
        }

        return (0..6).map { startOfWeek.plusDays(it.toLong()).format(formatter) }
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
    fun retryTransaction(transaction: Transaction) {
        if (transaction.offer == null) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                retryTransactionUseCase(transaction)
            } catch (e: Exception) {
                _snackbarMessage.value = e.message.toString()
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
