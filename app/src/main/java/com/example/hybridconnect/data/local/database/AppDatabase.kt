package com.example.hybridconnect.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.hybridconnect.data.local.dao.AgentCommissionDao
import com.example.hybridconnect.data.local.dao.AgentDao
import com.example.hybridconnect.data.local.dao.AutoReplyDao
import com.example.hybridconnect.data.local.dao.CommissionRateDao
import com.example.hybridconnect.data.local.dao.CustomerDao
import com.example.hybridconnect.data.local.dao.OfferDao
import com.example.hybridconnect.data.local.dao.PrefsDao
import com.example.hybridconnect.data.local.dao.SiteLinkDao
import com.example.hybridconnect.data.local.dao.SubscriptionPackageDao
import com.example.hybridconnect.data.local.dao.SubscriptionPlanDao
import com.example.hybridconnect.data.local.dao.TransactionDao
import com.example.hybridconnect.data.local.entity.AgentCommissionEntity
import com.example.hybridconnect.data.local.entity.AgentEntity
import com.example.hybridconnect.data.local.entity.AutoReplyEntity
import com.example.hybridconnect.data.local.entity.CommissionRateEntity
import com.example.hybridconnect.data.local.entity.CustomerEntity
import com.example.hybridconnect.data.local.entity.OfferEntity
import com.example.hybridconnect.data.local.entity.PrefEntity
import com.example.hybridconnect.data.local.entity.SiteLinkEntity
import com.example.hybridconnect.data.local.entity.SubscriptionPackageEntity
import com.example.hybridconnect.data.local.entity.SubscriptionPlanEntity
import com.example.hybridconnect.data.local.entity.TransactionEntity

@Database(
    entities = [
        CustomerEntity::class,
        OfferEntity::class,
        TransactionEntity::class,
        PrefEntity::class,
        SubscriptionPackageEntity::class,
        AgentEntity::class,
        CommissionRateEntity::class,
        AgentCommissionEntity::class,
        SiteLinkEntity::class,
        AutoReplyEntity::class,
        SubscriptionPlanEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun offerDao(): OfferDao
    abstract fun transactionDao(): TransactionDao
    abstract fun prefDao(): PrefsDao
    abstract fun subscriptionPackageDao(): SubscriptionPackageDao
    abstract fun agentDao(): AgentDao
    abstract fun getCommissionRateDao(): CommissionRateDao
    abstract fun agentCommissionDao(): AgentCommissionDao
    abstract fun siteLinkDao(): SiteLinkDao
    abstract fun autoReplyDao(): AutoReplyDao
    abstract fun subscriptionPlanDao(): SubscriptionPlanDao

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