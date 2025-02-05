package com.example.hybridconnect.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.hybridconnect.data.local.dao.AgentDao
import com.example.hybridconnect.data.local.dao.ConnectedAppDao
import com.example.hybridconnect.data.local.dao.PrefsDao
import com.example.hybridconnect.data.local.dao.TransactionDao
import com.example.hybridconnect.data.local.entity.AgentEntity
import com.example.hybridconnect.data.local.entity.ConnectedAppEntity
import com.example.hybridconnect.data.local.entity.PrefEntity
import com.example.hybridconnect.data.local.entity.TransactionEntity

@Database(
    entities = [
        PrefEntity::class,
        AgentEntity::class,
        TransactionEntity::class,
        ConnectedAppEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun prefDao(): PrefsDao
    abstract fun agentDao(): AgentDao
    abstract fun transactionDao(): TransactionDao
    abstract fun connectedAppDao(): ConnectedAppDao
}