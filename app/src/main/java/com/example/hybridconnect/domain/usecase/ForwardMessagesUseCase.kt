package com.example.hybridconnect.domain.usecase

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.TransactionRepository
import com.example.hybridconnect.domain.workers.MessageForwardingWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val TAG = "DialUssdUseCase"

class ForwardMessagesUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(transaction: Transaction) {
        try {
            transactionRepository.transactionQueue.add(transaction)
            Log.d(
                TAG,
                "Current transaction queue size: ${transactionRepository.transactionQueue.size}"
            )
            val forwardMessagesWork = OneTimeWorkRequestBuilder<MessageForwardingWorker>().build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    "MessageForwardingWork",
                    ExistingWorkPolicy.KEEP,
                    forwardMessagesWork
                )
        } catch (e: Exception) {
            Log.d(TAG, e.message, e)
        }
    }
}