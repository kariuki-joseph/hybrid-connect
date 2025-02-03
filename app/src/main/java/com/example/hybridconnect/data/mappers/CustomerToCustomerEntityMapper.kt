package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.CustomerEntity
import com.example.hybridconnect.domain.model.Customer

fun Customer.toEntity(): CustomerEntity {
    return CustomerEntity(
        id = this.id,
        name = this.name,
        phone = this.phone,
        accountBalance = this.accountBalance,
        lastPurchaseTime = this.lastPurchaseTime
    )
}