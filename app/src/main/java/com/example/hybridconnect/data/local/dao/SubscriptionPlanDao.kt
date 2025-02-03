package com.example.hybridconnect.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.hybridconnect.data.local.entity.SubscriptionPlanEntity

@Dao
interface SubscriptionPlanDao {
    @Insert
    suspend fun createPlan(subscriptionPlan: SubscriptionPlanEntity)

    @Query("SELECT * FROM subscription_plans")
    suspend fun getAllPlans(): List<SubscriptionPlanEntity>

    @Query("SELECT * FROM subscription_plans WHERE type = :type")
    suspend fun getByType(type: String): SubscriptionPlanEntity?

    @Update
    suspend fun updatePlan(subscriptionPlan: SubscriptionPlanEntity)

    @Delete
    suspend fun deletePlan(subscriptionPlan: SubscriptionPlanEntity)
}