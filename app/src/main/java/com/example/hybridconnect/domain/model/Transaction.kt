package com.example.hybridconnect.domain.model

import com.example.hybridconnect.domain.enums.TransactionStatus

data class Transaction(
    val id: Long,
    val mpesaCode: String? = null,
    val amount: Int,
    val mpesaMessage: String = "",
    val status: TransactionStatus = TransactionStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val offer: Offer?,
    val app: ConnectedApp? = null,
    val isForwarded: Boolean = false,
    val isDeleted: Boolean = false
) : Comparable<Transaction> {
    override fun compareTo(other: Transaction): Int {
        return createdAt.compareTo(other.createdAt)
    }
}