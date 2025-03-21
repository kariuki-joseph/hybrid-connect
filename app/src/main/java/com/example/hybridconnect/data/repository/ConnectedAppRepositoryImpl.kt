package com.example.hybridconnect.data.repository

import android.util.Log
import com.example.hybridconnect.data.local.dao.AppOfferDao
import com.example.hybridconnect.data.local.dao.ConnectedAppDao
import com.example.hybridconnect.data.local.entity.AppOfferEntity
import com.example.hybridconnect.data.mappers.toApiError
import com.example.hybridconnect.data.mappers.toDomain
import com.example.hybridconnect.data.mappers.toEntity
import com.example.hybridconnect.data.remote.api.ApiService
import com.example.hybridconnect.data.remote.api.request.CheckCanConnectToAppRequest
import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.model.ConnectedApp
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.repository.ConnectedAppRepository
import com.example.hybridconnect.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

private const val TAG = "ConnectedAppRepository"

class ConnectedAppRepositoryImpl @Inject constructor(
    private val connectedAppDao: ConnectedAppDao,
    private val appOfferDao: AppOfferDao,
    private val settingsRepository: SettingsRepository,
    private val apiService: ApiService,
) : ConnectedAppRepository {

    private val lastUsedIndexMap = ConcurrentHashMap<UUID, Int>()
    override fun getLastUsedIndexForOffer(offerId: UUID): Int {
        return lastUsedIndexMap[offerId] ?: -1 // return -1 if no index is found
    }

    override fun setLastUsedIndexForOffer(offerId: UUID, index: Int) {
        lastUsedIndexMap[offerId] = index
    }

    override fun getConnectedApps(): Flow<List<ConnectedApp>> =
        connectedAppDao.getAllConnectedApps().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getConnectedApp(connectId: String): ConnectedApp? {
        try {
            return connectedAppDao.getConnectedAppById(connectId)?.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, "getConnectedApp", e)
            throw e
        }
    }

    override suspend fun addConnectedApp(connectedApp: ConnectedApp) {
        try {
            connectedAppDao.addConnectedApp(connectedApp.toEntity())
        } catch (e: Exception) {
            Log.e(TAG, "Error adding connected app", e)
            throw e
        }
    }

    override suspend fun updateOnlineStatus(connectId: String, isOnline: Boolean) {
        try {
            connectedAppDao.updateOnlineStatus(connectId, isOnline)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating online status", e)
            throw e
        }
    }

    override suspend fun markAllAppsOffline() {
        try {
            connectedAppDao.markAllAppsOffline()
        } catch (e: Exception){
            Log.e(TAG, e.message, e)
        }
    }

    override suspend fun incrementMessagesSent(connectedApp: ConnectedApp) {
        try {
            connectedAppDao.incrementMessagesSent(connectedApp.connectId)
        } catch (e: Exception) {
            Log.e(TAG, "Error incrementing messages sent", e)
            throw e
        }
    }

    override suspend fun deleteConnectedApp(connectedApp: ConnectedApp) {
        try {
            connectedAppDao.deleteConnectedApp(connectedApp.connectId)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting connected app", e)
            throw e
        }
    }

    override suspend fun checkCanConnectToApp(connectId: String): Boolean {
        try {
            val agentId = settingsRepository.getSetting(AppSetting.AGENT_ID)
            val request = CheckCanConnectToAppRequest(
                agentId = agentId,
                connectId = connectId
            )
            val response = apiService.checkCanConnectToApp(request)
            if (!response.isSuccessful) {
                val errorCode = response.code()
                Log.e(TAG, "Error code: $errorCode, Error message: ${response.errorBody()}")
                throw Exception(
                    response.errorBody()?.toApiError()?.message
                        ?: "Error checking if can connect to app"
                )
            }

            return response.body()?.data?.canConnect ?: false
        } catch (e: Exception) {
            Log.e(TAG, "Error checking canAddConnectedApp", e)
            throw e
        }
    }

    override suspend fun addOffer(app: ConnectedApp, offer: Offer) {
        try {
            val appOffer = AppOfferEntity(
                appId = app.connectId,
                offerId = offer.id
            )
            appOfferDao.addAppOffer(appOffer)
        } catch (e: Exception) {
            Log.e(TAG, "addAppOffer", e)
            throw e
        }
    }

    override suspend fun deleteOffer(app: ConnectedApp, offer: Offer) {
        try {
            appOfferDao.deleteAppOffer(appId = app.connectId, offerId = offer.id)
        } catch (e: Exception) {
            Log.e(TAG, "deleteAppOffer", e)
            throw e
        }
    }

    override suspend fun getAppsByOffer(offer: Offer): List<ConnectedApp> {
        try {
            return appOfferDao.getConnectedAppsByOffer(offer.id).map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "getAppsByOffer", e)
            throw e
        }
    }

    override suspend fun getConnectedOffers(connectId: String): List<Offer> {
        try {
            return appOfferDao.getOffersByAppId(connectId).map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "getConnectedOffers", e)
            throw e
        }
    }

    override fun getAllConnectedOffersCount(): Flow<Map<String, Int>> {
        return appOfferDao.getAllConnectedOffersCount()
            .map { list -> list.associate { it.appId to it.offerCount } }
    }
}