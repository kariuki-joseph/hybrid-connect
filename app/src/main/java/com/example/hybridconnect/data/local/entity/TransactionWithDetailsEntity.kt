package com.example.hybridconnect.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class TransactionWithDetailsEntity(
    @Embedded val transaction: TransactionEntity,
    @Relation(
        parentColumn = "appId",
        entityColumn = "connectId"
    )
    val connectedApp: ConnectedAppEntity?,
    @Relation(
        parentColumn = "offerId",
        entityColumn = "id"
    )
    val offer: OfferEntity?
)
