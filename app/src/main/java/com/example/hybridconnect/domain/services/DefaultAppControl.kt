package com.example.hybridconnect.domain.services

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.enums.AppState
import com.example.hybridconnect.domain.repository.SettingsRepository
import com.example.hybridconnect.domain.services.interfaces.AppControl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DefaultAppControl @Inject constructor(
    private val context: Context,
    private val settingsRepository: SettingsRepository,
) : AppControl {

    private var _appState: MutableStateFlow<AppState> = MutableStateFlow(AppState.STATE_STOPPED)
    override val appState: StateFlow<AppState>
        get() = _appState.asStateFlow()

    override suspend fun syncAppStatus() {
        CoroutineScope(Dispatchers.IO).launch {
            val isSmsProcessingActive = isServiceRunning(context, SmsProcessingService::class.java)

            val newState = when {
                isSmsProcessingActive -> AppState.STATE_RUNNING
                else -> AppState.STATE_STOPPED
            }

            _appState.value = newState
            settingsRepository.saveSetting(AppSetting.APP_STATE, newState.name)
        }
    }

    init {
        initializeAppState()
    }

    override suspend fun startApp() {
        settingsRepository.saveSetting(AppSetting.APP_STATE, AppState.STATE_RUNNING.name)
        _appState.value = AppState.STATE_RUNNING
        startService()
    }

    override suspend fun pauseApp() {
        settingsRepository.saveSetting(AppSetting.APP_STATE, AppState.STATE_PAUSED.name)
        _appState.value = AppState.STATE_PAUSED
    }

    override suspend fun resumeApp() {
        settingsRepository.saveSetting(AppSetting.APP_STATE, AppState.STATE_RUNNING.name)
        _appState.value = AppState.STATE_RUNNING
    }

    override suspend fun stopApp() {
        settingsRepository.saveSetting(AppSetting.APP_STATE, AppState.STATE_STOPPED.name)
        _appState.value = AppState.STATE_STOPPED
        stopService()
    }

    private fun initializeAppState() {
        CoroutineScope(Dispatchers.IO).launch {
            var savedAppState = settingsRepository.getSetting(AppSetting.APP_STATE)
            if (savedAppState.isBlank()) savedAppState = AppState.STATE_STOPPED.name
            val appState = AppState.valueOf(savedAppState)
            _appState.value = appState
        }
    }

    private fun startService() {
        val smsProcessingServiceIntent = Intent(context, SmsProcessingService::class.java)
        ContextCompat.startForegroundService(context, smsProcessingServiceIntent)
    }

    private fun stopService() {
        val smsProcessingServiceIntent = Intent(context, SmsProcessingService::class.java)
        context.stopService(smsProcessingServiceIntent)
    }


    private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        @Suppress("DEPRECATION")
        return activityManager.getRunningServices(Int.MAX_VALUE)
            .any { it.service.className == serviceClass.name }
    }

}