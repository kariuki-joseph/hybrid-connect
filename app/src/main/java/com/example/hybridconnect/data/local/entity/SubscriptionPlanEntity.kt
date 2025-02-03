package com.example.hybridconnect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hybridconnect.domain.enums.SubscriptionType
import java.util.UUID

@Entity(tableName = "subscription_plans")
data class SubscriptionPlanEntity(
    @PrimaryKey val type: String,
    val subscriptionPackageId: UUID,
    val limit: Long
)