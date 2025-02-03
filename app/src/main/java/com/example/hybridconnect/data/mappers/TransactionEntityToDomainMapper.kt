package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.TransactionEntity
import com.example.hybridconnect.domain.model.Customer
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.model.Transaction

fun TransactionEntity.toDomain(customer: Customer, offer: Offer?): Transaction {
    return Transaction(
        id = this.id,
        amount = this.amount,
        time = this.time,
        mpesaMessage = this.mpesaMessage,
        responseMessage = this.responseMessage,
        status = this.status,
        customer = customer,
        offer = offer,
        type = this.type,
        retries = this.retries,
        createdAt = this.createdAt,
        rescheduleInfo = this.rescheduleInfo?.toDomain(),
    )
}