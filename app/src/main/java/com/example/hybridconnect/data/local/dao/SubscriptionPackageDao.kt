package com.example.hybridconnect.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.hybridconnect.data.local.entity.SubscriptionPackageEntity
import java.util.UUID

@Dao
interface SubscriptionPackageDao {
    @Insert
    suspend fun insert(subscription: SubscriptionPackageEntity)

    @Query("SELECT * FROM subscription_packages")
    suspend fun getAllSubscriptionPackages(): List<SubscriptionPackageEntity>

    @Query("SELECT * FROM subscription_packages WHERE id = :id")
    suspend fun getSubscriptionById(id: UUID): SubscriptionPackageEntity?

    @Update
    suspend fun update(subscription: SubscriptionPackageEntity)

    @Query("DELETE FROM subscription_packages WHERE 1")
    suspend fun deleteAllSubscriptionPackages()
}