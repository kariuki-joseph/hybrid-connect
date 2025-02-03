package com.example.hybridconnect.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.enums.TransactionType
import java.util.UUID

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"]
        ),
        ForeignKey(
            entity = OfferEntity::class,
            parentColumns = ["id"],
            childColumns = ["offerId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        androidx.room.Index(value = ["customerId"]),
        androidx.room.Index(value = ["offerId"])
    ]
)
data class TransactionEntity(
    @PrimaryKey
    val id: UUID,
    val customerId: Int,
    val offerId: UUID? = null,
    val amount: Int,
    val mpesaMessage: String,
    val time: Long,
    var responseMessage: String = "",
    var status: TransactionStatus = TransactionStatus.SCHEDULED,
    val type: TransactionType = TransactionType.TILL,
    val retries: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    @Embedded(prefix = "reschedule_") val rescheduleInfo: RescheduleInfoEntity?,
)