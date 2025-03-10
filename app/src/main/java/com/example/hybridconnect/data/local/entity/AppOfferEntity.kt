package com.example.hybridconnect.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "app_offers",
    foreignKeys = [
        ForeignKey(
            entity = ConnectedAppEntity::class,
            parentColumns = ["connectId"],
            childColumns = ["appId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = OfferEntity::class,
            parentColumns = ["id"],
            childColumns = ["offerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["appId"]),
        Index(value = ["offerId"]),
        Index(value = ["appId", "offerId"], unique = true)
    ]
)
data class AppOfferEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val appId: String,
    val offerId: UUID,
)