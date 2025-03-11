package com.example.hybridconnect.domain.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.hybridconnect.R
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.ConnectedAppRepository
import com.example.hybridconnect.domain.repository.TransactionRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MessageForwardingService"

@AndroidEntryPoint
class MessageForwardingService : Service() {
    companion object {
        const val CHANNEL_ID = "MessageForwardingServiceChannel"
    }

    @Inject
    lateinit var transactionRepository: TransactionRepository

    @Inject
    lateinit var socketService: SocketService

    @Inject
    lateinit var connectedAppRepository: ConnectedAppRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO)


    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, createNotification("Starting", "Initializing Service"))
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceScope.launch {
            processTransactions()
        }
        return START_STICKY
    }

    private suspend fun processTransactions() {
        while (true) {
            val transaction = transactionRepository.getOldestTransaction() ?: break
            val apps = connectedAppRepository.getConnectedApps().first()
            val activeApps = apps.filter { it.isOnline }

            if (activeApps.isEmpty()) {
                Log.e(TAG, "No connected apps to process transactions")
                delay(1000)
                continue
            }

            Log.d(TAG, "Processing transaction....")
            try {
                sendWebsocketMessage(transaction)
                transactionRepository.deleteTransaction(transaction.id)
            } catch (e: Exception) {
                Log.e(TAG, "Transaction ${transaction.id} failed, retrying later.", e)
                delay(2000)
            }
        }

        Log.d(TAG, "All transactions processed successfully.")
        stopSelf()
    }


    private suspend fun sendWebsocketMessage(transaction: Transaction) {
        val offer = transaction.offer ?: return
        val apps = connectedAppRepository.getAppsByOffer(offer)
        val activeApps = apps.filter { it.isOnline }

        if (activeApps.isEmpty()) {
            Log.e(TAG, "No connected apps available to process the message")
            return
        }

        // Fetch the last used index per offer and cycle to the next app
        val lastUsedIndex = connectedAppRepository.getLastUsedIndexForOffer(offer.id)
        val nextIndex = (lastUsedIndex + 1) % activeApps.size
        val selectedApp = activeApps[nextIndex]

        Log.d(TAG, "Sending message to ${selectedApp.connectId}")
        socketService.sendMessageToApp(selectedApp, transaction.message)

        // Update round-robin index per offer
        connectedAppRepository.incrementMessagesSent(selectedApp)
        connectedAppRepository.setLastUsedIndexForOffer(offer.id, nextIndex)
    }

    private fun createNotification(title: String, content: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.logo)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Message Forwarding Service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}