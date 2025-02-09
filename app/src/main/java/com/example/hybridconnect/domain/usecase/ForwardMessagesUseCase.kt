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
            val forwardMessagesWork = OneTimeWorkRequestBuilder<MessageForwardingWorker>()
                .addTag("MessageForwardingWork")
                .build()
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

    fun startMessageForwardingWorker() {
        val forwardMessagesWork = OneTimeWorkRequestBuilder<MessageForwardingWorker>()
            .addTag("MessageForwardingWork")
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "MessageForwardingWork",
                ExistingWorkPolicy.REPLACE,
                forwardMessagesWork
            )
        Log.d(TAG, "Message forwarding worker started.")
    }

    fun cancelMessageForwardingWork() {
        WorkManager.getInstance(context)
            .cancelAllWorkByTag("MessageForwardingWork")
        Log.d(TAG,
            "Message forwarding worker canceled."
        )
    }
}