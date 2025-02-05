package com.example.hybridconnect.domain.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.hybridconnect.data.di.WorkerEntryPoint
import com.example.hybridconnect.domain.repository.ConnectedAppRepository
import com.example.hybridconnect.domain.repository.TransactionRepository
import com.example.hybridconnect.domain.services.SocketService
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

private const val TAG = "MessageForwardingWorker"

class MessageForwardingWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    private var transactionRepository: TransactionRepository
    private var socketService: SocketService
    private var connectedAppRepository: ConnectedAppRepository

    init {
        val entryPoint = EntryPointAccessors.fromApplication(context, WorkerEntryPoint::class.java)
        transactionRepository = entryPoint.transactionRepository()
        socketService = entryPoint.socketService()
        connectedAppRepository = entryPoint.connectedAppRepository()
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            while (transactionRepository.transactionQueue.isNotEmpty()) {
                val apps = connectedAppRepository.getConnectedApps().first()
                val activeApps = apps.filter { it.isOnline }

                if (activeApps.isEmpty()) {
                    Log.e(TAG, "No connected apps available to process transactions")
                    continue
                }

                Log.d(
                    TAG,
                    "Processing transaction... Current queue size: ${transactionRepository.transactionQueue.size}"
                )

                val transaction = transactionRepository.transactionQueue.poll() ?: continue

                try {
                    sendWebSocketMessage(transaction.message)
                    transactionRepository.deleteTransaction(transaction.id)
                } catch (e: Exception) {
                    Log.e(TAG, "Transaction ${transaction.id} failed, adding it back to queue", e)
                    transactionRepository.transactionQueue.add(transaction)
                }
            }

            Log.d(TAG, "All transactions processed successfully.")
            Result.success()
        }
    }

    private suspend fun sendWebSocketMessage(message: String) {
        val apps = connectedAppRepository.getConnectedApps().first()
        val activeApps = apps.filter { it.isOnline }

        if (activeApps.isEmpty()) {
            Log.e(TAG, "No connected apps available to process the message")
            return
        }

        // Move to the next app in a round-robin order
        val lastAssignedIndex = (connectedAppRepository.lastAssignedIndex + 1) % activeApps.size
        val selectedApp = activeApps[lastAssignedIndex]
        Log.d(TAG, "Sending message to ${selectedApp.connectId}")
        socketService.sendMessageToApp(selectedApp, message)
        connectedAppRepository.incrementMessagesSent(selectedApp)
        connectedAppRepository.setLastAssignedIndex(lastAssignedIndex)
    }
}