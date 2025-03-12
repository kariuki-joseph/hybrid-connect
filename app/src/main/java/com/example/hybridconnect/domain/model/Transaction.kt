package com.example.hybridconnect.domain.model

data class Transaction(
    val id: Int,
    val message: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val offer: Offer?,
    val forwarded: Boolean = false,
) : Comparable<Transaction> {
    override fun compareTo(other: Transaction): Int {
        return createdAt.compareTo(other.createdAt)
    }
}