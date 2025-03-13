package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.TransactionEntity
import com.example.hybridconnect.domain.model.Transaction

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = this.id,
        mpesaCode = this.mpesaCode,
        amount = this.amount,
        message = this.mpesaMessage,
        status = this.status,
        offerId = this.offer?.id,
        createdAt = this.createdAt,
        isForwarded = this.isForwarded,
    )
}