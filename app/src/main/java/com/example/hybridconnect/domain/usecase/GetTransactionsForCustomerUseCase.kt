package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.model.Customer
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.TransactionRepository
import javax.inject.Inject

class GetTransactionsForCustomerUseCase @Inject constructor(
   private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(customer: Customer): List<Transaction>{
        return emptyList()
    }
}