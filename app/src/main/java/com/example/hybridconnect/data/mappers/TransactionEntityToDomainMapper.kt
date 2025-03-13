package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.TransactionEntity
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.model.Transaction

fun TransactionEntity.toDomain(offer: Offer?): Transaction {
    return Transaction(
        id = this.id,
        mpesaCode = this.mpesaCode,
        offer = offer,
        message = this.message,
        createdAt = this.createdAt,
        isForwarded = this.isForwarded
    )
}