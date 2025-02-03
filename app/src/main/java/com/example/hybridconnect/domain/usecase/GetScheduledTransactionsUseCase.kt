package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.TransactionRepository
import java.util.UUID
import javax.inject.Inject

class GetScheduledTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(parentTransactionId: UUID): List<Transaction>{
        return transactionRepository.getScheduledTransactionsForTransactionId(parentTransactionId)
    }
}