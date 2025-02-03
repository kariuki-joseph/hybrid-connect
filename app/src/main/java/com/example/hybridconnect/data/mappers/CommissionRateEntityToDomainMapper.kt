package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.CommissionRateEntity
import com.example.hybridconnect.domain.model.CommissionRate

fun CommissionRateEntity.toDomain(): CommissionRate {
    return CommissionRate(
        id = this.id,
        amount = this.amount,
        rate = this.rate,
        updatedAt = this.updatedAt,
    )
}