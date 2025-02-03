package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.RescheduleInfoEntity
import com.example.hybridconnect.domain.model.RescheduleInfo

fun RescheduleInfo.toEntity(): RescheduleInfoEntity {
    return RescheduleInfoEntity(
        parentTransactionId = parentTransactionId,
        rescheduleMode = rescheduleMode,
        time = time
    )
}