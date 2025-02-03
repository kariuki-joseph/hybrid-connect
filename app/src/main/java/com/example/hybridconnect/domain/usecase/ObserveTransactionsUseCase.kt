package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObserveTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    val transactions: StateFlow<List<Transaction>> = transactionRepository.transactions

    suspend operator fun invoke(): StateFlow<List<Transaction>> {
        transactionRepository.getTransactions()
        return transactionRepository.transactions
    }
}