package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.TransactionRepository
import javax.inject.Inject

class GetTransactionsForPeriodUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(startTime: Long, endTime: Long): List<Transaction> {
        return transactionRepository.getTransactionsBetween(startTime, endTime)
    }
}