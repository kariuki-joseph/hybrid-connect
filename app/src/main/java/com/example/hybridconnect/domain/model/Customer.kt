package com.example.hybridconnect.domain.model

data class Customer(
    val id: Int,
    val name: String,
    val phone: String,
    val accountBalance: Int = 0,
    val lastPurchaseTime: Long = System.currentTimeMillis()
)