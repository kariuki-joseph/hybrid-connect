package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.model.Customer
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.CustomerRepository
import com.example.hybridconnect.domain.repository.TransactionRepository
import javax.inject.Inject

class CreateTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val customerRepository: CustomerRepository,
) {
    suspend operator fun invoke(transaction: Transaction): Transaction {
        transactionRepository.createTransaction(transaction)
        return transaction
    }

    private suspend fun updateLastPurchaseTime(customer: Customer) {
        customerRepository.updateLastPurchaseTime(customer, System.currentTimeMillis())
    }
}
