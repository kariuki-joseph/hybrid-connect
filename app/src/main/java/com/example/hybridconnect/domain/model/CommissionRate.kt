package com.example.hybridconnect.domain.model

import java.util.UUID

data class CommissionRate (
    val id: UUID,
    val amount: Int,
    val rate: Double,
    val updatedAt: Long
)