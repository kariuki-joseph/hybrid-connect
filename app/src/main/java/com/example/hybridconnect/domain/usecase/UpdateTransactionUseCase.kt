package com.example.hybridconnect.domain.usecase

import android.util.Log
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.TransactionRepository
import javax.inject.Inject

private const val TAG = "UpdateTransactionStatusUseCase"

class UpdateTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(updatedTransaction: Transaction) {
        try {
            transactionRepository.updateTransaction(updatedTransaction)
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            throw e
        }
    }
}