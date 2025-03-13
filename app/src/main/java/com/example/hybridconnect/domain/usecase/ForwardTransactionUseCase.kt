package com.example.hybridconnect.domain.usecase

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.TransactionRepository
import com.example.hybridconnect.domain.services.MessageForwardingService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val TAG = "ForwardMessagesUseCase"

class ForwardTransactionUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(transaction: Transaction) {
        try {
            if (transactionRepository.transactionQueue.contains(transaction)) {
                throw Exception("Transaction has already been queued up for forwarding. Aborting")
            }

            transactionRepository.transactionQueue.add(transaction)
            Log.d(
                TAG,
                "Current transaction queue size: ${transactionRepository.transactionQueue.size}"
            )

            val forwardingIntent = Intent(context, MessageForwardingService::class.java)
            context.startForegroundService(forwardingIntent)
        } catch (e: Exception) {
            Log.d(TAG, e.message, e)
        }
    }
}