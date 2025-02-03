package com.example.hybridconnect.domain.model

import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.enums.TransactionType
import java.util.UUID

data class Transaction(
    val id: UUID = UUID.randomUUID(),
    val amount: Int,
    val time: Long = System.currentTimeMillis(),
    val mpesaMessage: String = "",
    val responseMessage: String = "",
    val status: TransactionStatus = TransactionStatus.SCHEDULED,
    val customer: Customer,
    val offer: Offer?,
    val type: TransactionType = TransactionType.TILL,
    var retries: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val rescheduleInfo: RescheduleInfo? = null,
): Comparable<Transaction> {
    override fun compareTo(other: Transaction): Int {
        return time.compareTo(other.time)
    }
}