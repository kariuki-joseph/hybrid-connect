package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.RescheduleInfoEntity
import com.example.hybridconnect.domain.model.RescheduleInfo

fun RescheduleInfoEntity.toDomain(): RescheduleInfo {
    return RescheduleInfo(
        parentTransactionId = parentTransactionId,
        rescheduleMode = rescheduleMode,
        time = this.time,
    )
}