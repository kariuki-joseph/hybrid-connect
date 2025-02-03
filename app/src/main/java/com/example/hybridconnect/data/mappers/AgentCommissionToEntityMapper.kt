package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.AgentCommissionEntity
import com.example.hybridconnect.domain.model.AgentCommission

fun AgentCommission.toEntity(): AgentCommissionEntity {
    return AgentCommissionEntity(
        date = this.date,
        amount = this.amount
    )
}