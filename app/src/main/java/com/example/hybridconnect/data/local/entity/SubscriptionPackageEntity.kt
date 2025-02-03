package com.example.hybridconnect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hybridconnect.domain.enums.SubscriptionType
import java.util.UUID

@Entity(tableName = "subscription_packages")
data class SubscriptionPackageEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val price: Int,
    val description: String,
    val limit: Double,
    val type: SubscriptionType,
    val updatedAt: Long? = null
)