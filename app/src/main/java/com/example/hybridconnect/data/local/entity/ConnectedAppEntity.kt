package com.example.hybridconnect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "connected_apps")
data class ConnectedAppEntity(
    @PrimaryKey val connectId: String,
    val appName: String,
    val isOnline: Boolean,
    val messagesSent: Int = 0,
)