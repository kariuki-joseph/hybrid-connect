package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.TransactionEntity
import com.example.hybridconnect.domain.model.Transaction

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = this.id,
        amount = this.amount,
        time = this.time,
        mpesaMessage = this.mpesaMessage,
        responseMessage = this.responseMessage,
        customerId = this.customer.id,
        offerId = this.offer?.id,
        status = this.status,
        type = this.type,
        retries = this.retries,
        createdAt = this.createdAt,
        rescheduleInfo = this.rescheduleInfo?.toEntity()
    )
}