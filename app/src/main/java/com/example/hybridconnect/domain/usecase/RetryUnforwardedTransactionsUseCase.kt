package com.example.hybridconnect.domain.usecase

import android.util.Log
import com.example.hybridconnect.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private const val TAG = "RetryUnforwardedTransactionsUseCase"

class RetryUnforwardedTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val forwardTransactionUseCase: ForwardTransactionUseCase,
) {
    suspend operator fun invoke() {
        try {
            val transactions = transactionRepository.transactionQueueFlow.first()
            transactions.forEach { transaction ->
                forwardTransactionUseCase(transaction)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing unforwarded transactions", e)
        }
    }
}