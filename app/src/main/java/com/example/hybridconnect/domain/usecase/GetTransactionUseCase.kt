package com.example.hybridconnect.domain.usecase

import android.util.Log
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.TransactionRepository
import javax.inject.Inject

private const val TAG = "GetTransactionUseCase"

class GetTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(transactionId: Long): Transaction {
        try {
            transactionRepository.getTransactions().collect { list ->
                list.filter { it.id == transactionId }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            throw e
        }
    }
}