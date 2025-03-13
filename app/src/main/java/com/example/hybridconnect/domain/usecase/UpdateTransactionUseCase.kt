package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.repository.TransactionRepository
import java.util.UUID
import javax.inject.Inject

class UpdateTransactionStatusUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(transactionId: UUID, status: TransactionStatus) {

    }
}