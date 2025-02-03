package com.example.hybridconnect.domain.model

import com.example.hybridconnect.domain.enums.RescheduleMode
import java.util.UUID

data class RescheduleInfo(
    val parentTransactionId: UUID? = null,
    val time: Long,
    val rescheduleMode: RescheduleMode = RescheduleMode.ONCE,
)