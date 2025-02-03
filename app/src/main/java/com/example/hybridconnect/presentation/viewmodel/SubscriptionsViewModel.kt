package com.example.hybridconnect.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridconnect.domain.enums.SubscriptionType
import com.example.hybridconnect.domain.model.SubscriptionPackage
import com.example.hybridconnect.domain.model.SubscriptionPlan
import com.example.hybridconnect.domain.repository.PaymentRepository
import com.example.hybridconnect.domain.repository.SubscriptionPlanRepository
import com.example.hybridconnect.domain.usecase.subscriptions.GetSubscriptionPackagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val getSubscriptionPackagesUseCase: GetSubscriptionPackagesUseCase,
    private val paymentRepository: PaymentRepository,
    private val subscriptionPlanRepository: SubscriptionPlanRepository,
) : ViewModel() {
    val packages: StateFlow<List<SubscriptionPackage>> =
        getSubscriptionPackagesUseCase.subscriptions

    private val activePlans: StateFlow<List<SubscriptionPlan>> =
        subscriptionPlanRepository.activePlansFlow

    private val _unlimitedPlanLimit = MutableStateFlow(0L)
    val unlimitedPlanLimit: StateFlow<Long> = _unlimitedPlanLimit.asStateFlow()

    private val _limitedPlanLimit = MutableStateFlow(0L)
    val limitedPlanLimit: StateFlow<Long> = _limitedPlanLimit.asStateFlow()

    private var countdownJob: Job? = null

    private val _chosenPackage = MutableStateFlow<SubscriptionPackage?>(null)
    val chosenPackage: StateFlow<SubscriptionPackage?> = _chosenPackage.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoadingSubscriptionPlans = MutableStateFlow(false)
    val isLoadingSubscriptionPlans: StateFlow<Boolean> = _isLoadingSubscriptionPlans

    init {
        getSubscriptionPackages()
        getSubscriptionPlans()
    }

    private fun getSubscriptionPackages() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    _isLoadingSubscriptionPlans.value = true
                    getSubscriptionPackagesUseCase()
                    _isLoadingSubscriptionPlans.value = false
                }
            } catch (e: Exception) {
                _isLoadingSubscriptionPlans.value = false
                _errorMessage.value = e.message
            }
        }
    }

    fun setChosenPlan(subscriptionPackage: SubscriptionPackage) {
        paymentRepository.setSubscription(subscriptionPackage)

        if (_chosenPackage.value == subscriptionPackage) {
            _chosenPackage.value = null
            return
        }
        _chosenPackage.value = subscriptionPackage
    }

    private fun getSubscriptionPlans() {
        viewModelScope.launch {
            subscriptionPlanRepository.getActivePlans()
            activePlans.collect { plans ->
                // Handle UNLIMITED plan
                val unlimitedPlan = plans.firstOrNull {
                    it.type == SubscriptionType.UNLIMITED && it.limit > System.currentTimeMillis()
                }
                if (unlimitedPlan != null) {
                    startCountdownTimer(unlimitedPlan.limit)
                } else {
                    stopCountdownTimer()
                }

                // Handle LIMITED plan
                val limitedPlan = plans.firstOrNull {
                    it.type == SubscriptionType.LIMITED && it.limit > 0
                }
                _limitedPlanLimit.value = limitedPlan?.limit ?: 0L
            }
        }
    }

    private fun startCountdownTimer(limit: Long) {
        stopCountdownTimer()
        countdownJob = viewModelScope.launch {
            while (true) {
                val remainingTime = limit - System.currentTimeMillis()
                if (remainingTime <= 0) {
                    _unlimitedPlanLimit.value = 0L
                    stopCountdownTimer()
                    break
                } else {
                    _unlimitedPlanLimit.value = remainingTime
                }
                delay(1000L) // Update every second
            }
        }
    }

    private fun stopCountdownTimer() {
        countdownJob?.cancel()
        countdownJob = null
    }

    fun resetSnackbarMessage() {
        _errorMessage.value = null
    }
}