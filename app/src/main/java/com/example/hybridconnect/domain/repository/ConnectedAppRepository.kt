package com.example.hybridconnect.domain.repository

import com.example.hybridconnect.domain.model.ConnectedApp
import com.example.hybridconnect.domain.model.Offer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

interface ConnectedAppRepository {
    fun getConnectedApps(): Flow<List<ConnectedApp>>
    suspend fun getConnectedApp(connectId: String): ConnectedApp?
    suspend fun addConnectedApp(connectedApp: ConnectedApp)
    suspend fun updateOnlineStatus(connectId: String, isOnline: Boolean)
    suspend fun markAllAppsOffline()
    suspend fun incrementMessagesSent(connectedApp: ConnectedApp)
    suspend fun deleteConnectedApp(connectedApp: ConnectedApp)
    suspend fun checkCanConnectToApp(connectId: String): Boolean
    suspend fun addOffer(app: ConnectedApp, offer: Offer)
    suspend fun deleteOffer(app: ConnectedApp, offer: Offer)
    suspend fun getAppsByOffer(offer: Offer): List<ConnectedApp>
    suspend fun getConnectedOffers(connectId: String): List<Offer>
    fun getAllConnectedOffersCount(): Flow<Map<String, Int>>

    // Store the last used index per offer
    fun getLastUsedIndexForOffer(offerId: UUID): Int
    fun setLastUsedIndexForOffer(offerId: UUID, index: Int)
}