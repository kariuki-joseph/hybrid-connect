package com.example.hybridconnect.domain.usecase

import android.util.Log
import com.example.hybridconnect.domain.repository.TransactionRepository
import javax.inject.Inject

private const val TAG = "RetryUnforwardedTransactionsUseCase"

class RetryUnforwardedTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val forwardTransactionUseCase: ForwardTransactionUseCase,
) {
    suspend operator fun invoke() {
        try {
            transactionRepository.transactionQueueFlow.collect { transactions ->
                transactions.forEach { transaction ->
                    forwardTransactionUseCase(transaction)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing unforwarded transactions", e)
        }
    }
}