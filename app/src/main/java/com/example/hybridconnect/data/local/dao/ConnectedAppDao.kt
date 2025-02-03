package com.example.hybridconnect.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.hybridconnect.data.local.entity.ConnectedAppEntity

@Dao
interface ConnectedAppDao {
    @Insert
    suspend fun addConnectedApp(connectedApp: ConnectedAppEntity)

    @Query("SELECT * FROM connected_apps")
    suspend fun getAllConnectedApps(): List<ConnectedAppEntity>

    @Query("UPDATE connected_apps SET messagesSent = messagesSent + 1 WHERE connectId = :connectId")
    suspend fun incrementMessagesSent(connectId: String)

    @Query("DELETE FROM connected_apps WHERE connectId = :connectId")
    suspend fun deleteConnectedApp(connectId: String)
}