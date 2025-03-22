package com.example.hybridconnect.domain.model

import com.example.hybridconnect.domain.enums.TransactionStatus

data class TransactionStatusCount(
    val appId: String?,
    val status: TransactionStatus,
    val count: Int
)