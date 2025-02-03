package com.example.hybridconnect.presentation.dto

import com.example.hybridconnect.domain.model.Customer
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.model.Transaction

data class TransactionDTO(
    val transaction: Transaction,
    val customer: Customer,
    val offer: Offer?
)