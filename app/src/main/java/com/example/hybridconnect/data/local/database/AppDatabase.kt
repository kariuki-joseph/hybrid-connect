package com.example.hybridconnect.data.local.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.hybridconnect.data.local.dao.AgentDao
import com.example.hybridconnect.data.local.dao.AppOfferDao
import com.example.hybridconnect.data.local.dao.ConnectedAppDao
import com.example.hybridconnect.data.local.dao.OfferDao
import com.example.hybridconnect.data.local.dao.PrefsDao
import com.example.hybridconnect.data.local.dao.TransactionDao
import com.example.hybridconnect.data.local.entity.AgentEntity
import com.example.hybridconnect.data.local.entity.AppOfferEntity
import com.example.hybridconnect.data.local.entity.ConnectedAppEntity
import com.example.hybridconnect.data.local.entity.OfferEntity
import com.example.hybridconnect.data.local.entity.PrefEntity
import com.example.hybridconnect.data.local.entity.TransactionEntity

@Database(
    entities = [
        PrefEntity::class,
        AgentEntity::class,
        OfferEntity::class,
        TransactionEntity::class,
        ConnectedAppEntity::class,
        AppOfferEntity::class
    ],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2), // Offers
        AutoMigration(from = 2, to = 3) // Connected App Offers
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun prefDao(): PrefsDao
    abstract fun agentDao(): AgentDao
    abstract fun offerDao(): OfferDao
    abstract fun transactionDao(): TransactionDao
    abstract fun connectedAppDao(): ConnectedAppDao
    abstract fun appOfferDao(): AppOfferDao
}