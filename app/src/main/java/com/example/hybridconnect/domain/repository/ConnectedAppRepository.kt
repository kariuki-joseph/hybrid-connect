package com.example.hybridconnect.domain.repository

import com.example.hybridconnect.domain.model.ConnectedApp
import kotlinx.coroutines.flow.StateFlow

interface ConnectedAppRepository {
    suspend fun getConnectedApps(): StateFlow<List<ConnectedApp>>
    suspend fun getConnectedApp(connectId: String): ConnectedApp?
    suspend fun addConnectedApp(connectedApp: ConnectedApp)
    suspend fun updateOnlineStatus(connectId: String, isOnline: Boolean)
    suspend fun incrementMessagesSent(connectedApp: ConnectedApp)
    suspend fun deleteConnectedApp(connectedApp: ConnectedApp)
    suspend fun checkCanConnectToApp(connectId: String): Boolean
}