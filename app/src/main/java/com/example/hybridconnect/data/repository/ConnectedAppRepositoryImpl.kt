package com.example.hybridconnect.data.repository

import android.util.Log
import com.example.hybridconnect.data.local.dao.ConnectedAppDao
import com.example.hybridconnect.data.mappers.toApiError
import com.example.hybridconnect.data.mappers.toDomain
import com.example.hybridconnect.data.mappers.toEntity
import com.example.hybridconnect.data.remote.api.ApiService
import com.example.hybridconnect.data.remote.api.request.CheckCanConnectToAppRequest
import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.model.ConnectedApp
import com.example.hybridconnect.domain.repository.ConnectedAppRepository
import com.example.hybridconnect.domain.repository.PrefsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ConnectedAppRepository"

class ConnectedAppRepositoryImpl @Inject constructor(
    private val connectedAppDao: ConnectedAppDao,
    private val prefsRepository: PrefsRepository,
    private val apiService: ApiService,
) : ConnectedAppRepository {

    private val _connectedAppsFlow = MutableStateFlow<List<ConnectedApp>>(emptyList())
    override suspend fun getConnectedApps(): StateFlow<List<ConnectedApp>> = _connectedAppsFlow

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _connectedAppsFlow.value = connectedAppDao.getAllConnectedApps().map { it.toDomain() }
        }
    }

    override suspend fun addConnectedApp(connectedApp: ConnectedApp) {
        try {
            connectedAppDao.addConnectedApp(connectedApp.toEntity())
            _connectedAppsFlow.value = connectedAppDao.getAllConnectedApps().map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding connected app", e)
            throw e
        }
    }

    override suspend fun incrementMessagesSent(connectedApp: ConnectedApp) {
        try {
            connectedAppDao.incrementMessagesSent(connectedApp.connectId)
            _connectedAppsFlow.value = connectedAppDao.getAllConnectedApps().map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "Error incrementing messages sent", e)
            throw e
        }
    }

    override suspend fun deleteConnectedApp(connectedApp: ConnectedApp) {
        try {
            connectedAppDao.deleteConnectedApp(connectedApp.connectId)
            _connectedAppsFlow.value = connectedAppDao.getAllConnectedApps().map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting connected app", e)
            throw e
        }
    }

    override suspend fun checkCanConnectToApp(connectId: String): Boolean {
        try {
            val agentId = prefsRepository.getSetting(AppSetting.AGENT_ID)
            val request = CheckCanConnectToAppRequest(
                agentId = agentId,
                connectId = connectId
            )
            val response = apiService.checkCanConnectToApp(request)
            if (!response.isSuccessful) {
                val errorCode = response.code()
                Log.e(TAG, "Error code: $errorCode, Error message: ${response.errorBody()}")
                throw Exception(
                    response.errorBody()?.toApiError()?.message ?: "Error checking if can connect to app"
                )
            }

            return response.body()?.data?.canConnect ?: false
        } catch (e: Exception){
            Log.e(TAG, "Error checking canAddConnectedApp", e)
            throw e
        }
    }
}