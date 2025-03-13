package com.example.hybridconnect.domain.model

data class Transaction(
    val id: Int,
    val mpesaCode: String? = null,
    val message: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val offer: Offer?,
    val isForwarded: Boolean = false,
) : Comparable<Transaction> {
    override fun compareTo(other: Transaction): Int {
        return createdAt.compareTo(other.createdAt)
    }
}