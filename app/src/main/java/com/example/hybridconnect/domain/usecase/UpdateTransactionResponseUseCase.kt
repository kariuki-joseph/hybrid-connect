package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.repository.TransactionRepository
import java.util.UUID
import javax.inject.Inject

class UpdateTransactionResponseUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(transactionId: UUID, response: String) {
        transactionRepository.updateTransactionResponse(transactionId, response)
    }
}