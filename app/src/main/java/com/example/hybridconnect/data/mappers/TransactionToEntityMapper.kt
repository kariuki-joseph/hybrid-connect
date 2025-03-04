package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.TransactionEntity
import com.example.hybridconnect.domain.model.Transaction

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = this.id,
        message = this.message,
        createdAt = this.createdAt
    )
}