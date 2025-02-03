package com.example.hybridconnect.domain.usecase

import android.util.Log
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.TransactionRepository
import java.util.UUID
import javax.inject.Inject

private const val TAG = "GetTransactionUseCase"

class GetTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(transactionId: UUID): Transaction {
        try {
            return transactionRepository.getTransactionById(transactionId)
                ?: throw Exception("No transaction with the given ID was found")
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            throw e
        }
    }
}