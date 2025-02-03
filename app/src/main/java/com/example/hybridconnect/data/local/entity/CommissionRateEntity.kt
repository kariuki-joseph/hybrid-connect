package com.example.hybridconnect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "commission_rates"
)
data class CommissionRateEntity (
    @PrimaryKey
    val id: UUID,
    val amount: Int,
    val rate: Double = 0.1,
    val updatedAt: Long
)