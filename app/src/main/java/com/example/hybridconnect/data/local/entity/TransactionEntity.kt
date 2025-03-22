package com.example.hybridconnect.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.hybridconnect.domain.enums.TransactionStatus
import java.util.UUID

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = OfferEntity::class,
            parentColumns = ["id"],
            childColumns = ["offerId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = ConnectedAppEntity::class,
            parentColumns = ["connectId"],
            childColumns = ["appId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["offerId"]),
        Index(value = ["mpesaCode"], unique = true),
        Index(value = ["appId"])
    ]
)

data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mpesaCode: String? = null,
    @ColumnInfo(defaultValue = "0")
    val amount: Int,
    @ColumnInfo(defaultValue = "PENDING")
    val status: TransactionStatus,
    val offerId: UUID?,
    val appId: String?,
    val message: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(defaultValue = "0")
    val isForwarded: Boolean = false,
    @ColumnInfo(defaultValue = "0")
    val isDeleted: Boolean = false,
)
