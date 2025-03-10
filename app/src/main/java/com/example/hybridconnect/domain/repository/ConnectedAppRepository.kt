package com.example.hybridconnect.domain.repository

import com.example.hybridconnect.domain.model.ConnectedApp
import com.example.hybridconnect.domain.model.Offer
import kotlinx.coroutines.flow.StateFlow

interface ConnectedAppRepository {
    val lastAssignedIndex: Int
    fun setLastAssignedIndex(index: Int)
    suspend fun getConnectedApps(): StateFlow<List<ConnectedApp>>
    suspend fun getConnectedApp(connectId: String): ConnectedApp?
    suspend fun addConnectedApp(connectedApp: ConnectedApp)
    suspend fun updateOnlineStatus(connectId: String, isOnline: Boolean)
    suspend fun incrementMessagesSent(connectedApp: ConnectedApp)
    suspend fun deleteConnectedApp(connectedApp: ConnectedApp)
    suspend fun checkCanConnectToApp(connectId: String): Boolean
    suspend fun addOffer(app: ConnectedApp, offer: Offer)
    suspend fun deleteOffer(app: ConnectedApp, offer: Offer)
    suspend fun getAppByOffer(offer: Offer): ConnectedApp?
    suspend fun getConnectedOffers(connectId: String): List<Offer>
}