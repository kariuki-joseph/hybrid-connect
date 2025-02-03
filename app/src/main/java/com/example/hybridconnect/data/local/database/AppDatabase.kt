package com.example.hybridconnect.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.hybridconnect.data.local.dao.AgentDao
import com.example.hybridconnect.data.local.dao.ConnectedAppDao
import com.example.hybridconnect.data.local.dao.PrefsDao
import com.example.hybridconnect.data.local.entity.AgentEntity
import com.example.hybridconnect.data.local.entity.ConnectedAppEntity
import com.example.hybridconnect.data.local.entity.PrefEntity

@Database(
    entities = [
        PrefEntity::class,
        AgentEntity::class,
        ConnectedAppEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun prefDao(): PrefsDao
    abstract fun agentDao(): AgentDao
    abstract fun connectedAppDao(): ConnectedAppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}