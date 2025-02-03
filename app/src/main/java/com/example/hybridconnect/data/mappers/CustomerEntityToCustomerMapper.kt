package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.CustomerEntity
import com.example.hybridconnect.domain.model.Customer

fun CustomerEntity.toDomain(): Customer {
    return Customer(
        id = this.id,
        name = this.name,
        phone = this.phone,
        accountBalance = this.accountBalance,
        lastPurchaseTime = this.lastPurchaseTime
    )
}