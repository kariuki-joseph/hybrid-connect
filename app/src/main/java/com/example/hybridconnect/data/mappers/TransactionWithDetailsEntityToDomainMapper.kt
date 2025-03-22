package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.TransactionWithDetailsEntity
import com.example.hybridconnect.domain.model.Transaction

fun TransactionWithDetailsEntity.toDomain(): Transaction {
    return Transaction(
        id = transaction.id,
        mpesaCode = transaction.mpesaCode,
        amount = transaction.amount,
        mpesaMessage = transaction.message,
        status = transaction.status,
        createdAt = transaction.createdAt,
        offer = offer?.toDomain(),
        app = connectedApp?.toDomain(),
        isForwarded = transaction.isForwarded,
        isDeleted = transaction.isDeleted,
    )
}