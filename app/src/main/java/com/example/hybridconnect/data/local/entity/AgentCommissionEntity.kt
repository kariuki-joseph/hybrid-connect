package com.example.hybridconnect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "agent_commissions"
)
data class AgentCommissionEntity(
    @PrimaryKey
    val date: String,
    val amount: Double
)
