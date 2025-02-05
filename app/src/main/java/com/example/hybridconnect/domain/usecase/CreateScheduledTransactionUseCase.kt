package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.TransactionRepository
import javax.inject.Inject

class CreateScheduledTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(
        originalTransaction: Transaction,
        scheduledTransaction: Transaction,
    ) {

    }
}