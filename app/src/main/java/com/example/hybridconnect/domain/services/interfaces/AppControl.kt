package com.example.hybridconnect.domain.services.interfaces

import com.example.hybridconnect.domain.enums.AppState
import kotlinx.coroutines.flow.StateFlow

interface AppControl {
    val appState: StateFlow<AppState>
    suspend fun syncAppStatus()
    suspend fun startApp()
    suspend fun pauseApp()
    suspend fun resumeApp()
    suspend fun stopApp()
}