package com.example.hybridconnect.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridconnect.domain.enums.SimCard
import com.example.hybridconnect.domain.model.Agent
import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.repository.AuthRepository
import com.example.hybridconnect.domain.repository.SettingsRepository
import com.example.hybridconnect.presentation.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _ussdSimCard = MutableStateFlow(SimCard.SIM_ONE)
    val ussdSimCard: StateFlow<SimCard> = _ussdSimCard

    private val _paymentSimCards = MutableStateFlow<List<SimCard>>(emptyList())
    val paymentSimCards: StateFlow<List<SimCard>> = _paymentSimCards

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    private val _initialRoute = MutableStateFlow<Route>(Route.SplashScreen)
    val initialRoute: StateFlow<Route> = _initialRoute

    val agent: StateFlow<Agent?> = authRepository.agent

    private val _settings = MutableStateFlow<Map<AppSetting, Boolean>>(emptyMap())
    val settings: StateFlow<Map<AppSetting, Boolean>> = _settings

    init {
        getAgent()
        getSettings()
    }

    private fun getAgent() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                authRepository.fetchAgent()
            }catch (e: Exception){
                _snackbarMessage.value = e.message.toString()
            }
        }
    }

    private fun getSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            val settingsMap = mutableMapOf<AppSetting, Boolean>()
            AppSetting.entries.forEach { setting ->
                val settingValue = settingsRepository.getSetting(setting).toBoolean()
                settingsMap[setting] = settingValue
            }

            _settings.value = settingsMap
        }
    }

    fun updateSetting(setting: AppSetting, value: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedSettings = _settings.value.toMutableMap()
            // handle special cases
            if (setting == AppSetting.DIAL_USSD_VIA_SIM_1 && value) {
                settingsRepository.saveSetting(AppSetting.DIAL_USSD_VIA_SIM_2, false.toString())
                updatedSettings[AppSetting.DIAL_USSD_VIA_SIM_2] = false
            } else if (setting == AppSetting.DIAL_USSD_VIA_SIM_2 && value) {
                settingsRepository.saveSetting(AppSetting.DIAL_USSD_VIA_SIM_1, false.toString())
                updatedSettings[AppSetting.DIAL_USSD_VIA_SIM_1] = false
            }

            updatedSettings[setting] = value
            settingsRepository.saveSetting(setting, value.toString())
            _settings.value = updatedSettings
        }
    }

    fun setUssdSimCard(simCard: SimCard) {
        _ussdSimCard.value = simCard
    }

    fun onPaymentSimChanged(simCard: SimCard) {
        val currentSimCards = _paymentSimCards.value.toMutableList()
        if (currentSimCards.contains(simCard)) {
            currentSimCards.remove(simCard)
        } else {
            currentSimCards.add(simCard)
        }
        _paymentSimCards.value = currentSimCards
    }

    fun getInitialDestination() {
        viewModelScope.launch(Dispatchers.IO) {
            val isOnBoarded = settingsRepository.isOnBoardingCompleted()
            val isLoggedIn = settingsRepository.getSetting(AppSetting.AGENT_ID).isNotEmpty()

            if (isOnBoarded && isLoggedIn) {
                _initialRoute.value = Route.Home
            }
            if (isOnBoarded && !isLoggedIn) {
                delay(1500)
                _initialRoute.value = Route.Login
            }
            if (!isOnBoarded && !isLoggedIn) {
                delay(200)
                _initialRoute.value = Route.OnboardingScreen1
            }
        }
    }

    fun setOnboardingCompleted() {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.setOnBoardingCompleted()
        }
    }

    fun updateSimSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.setUssdSimCard(_ussdSimCard.value)
            settingsRepository.setPaymentSimCards(_paymentSimCards.value)
            _snackbarMessage.value = "Sim settings updated"
        }
    }

    fun resetSnackbarMessage() {
        _snackbarMessage.value = null
    }
}