package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.AgentCommissionEntity
import com.example.hybridconnect.domain.model.AgentCommission

fun AgentCommissionEntity.toDomain(): AgentCommission {
    return AgentCommission(
        date = this.date,
        amount = this.amount
    )
}