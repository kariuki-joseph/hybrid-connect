package com.example.hybridconnect.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.hybridconnect.data.local.entity.ConnectedAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConnectedAppDao {
    @Insert
    suspend fun addConnectedApp(connectedApp: ConnectedAppEntity)

    @Query("SELECT * FROM connected_apps WHERE connectId = :connectId LIMIT 1")
    suspend fun getConnectedAppById(connectId: String): ConnectedAppEntity?

    @Query("SELECT * FROM connected_apps")
    fun getAllConnectedApps(): Flow<List<ConnectedAppEntity>>

    @Query("UPDATE connected_apps SET messagesSent = messagesSent + 1 WHERE connectId = :connectId")
    suspend fun incrementMessagesSent(connectId: String)

    @Query("UPDATE connected_apps SET isOnline = :isOnline WHERE connectId = :connectId")
    suspend fun updateOnlineStatus(connectId: String, isOnline: Boolean)

    @Query("UPDATE connected_apps SET isOnline = 0")
    suspend fun markAllAppsOffline()

    @Query("DELETE FROM connected_apps WHERE connectId = :connectId")
    suspend fun deleteConnectedApp(connectId: String)
}