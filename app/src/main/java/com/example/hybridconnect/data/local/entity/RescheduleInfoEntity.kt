package com.example.hybridconnect.data.local.entity

import com.example.hybridconnect.domain.enums.RescheduleMode
import java.util.UUID

data class RescheduleInfoEntity(
    val parentTransactionId: UUID? = null,
    val rescheduleMode: RescheduleMode,
    val time: Long,
)