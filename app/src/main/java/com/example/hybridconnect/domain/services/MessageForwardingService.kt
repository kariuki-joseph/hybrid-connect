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
import com.example.hybridconnect.domain.enums.AppState
import com.example.hybridconnect.domain.exception.UnavailableOfferException
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.ConnectedAppRepository
import com.example.hybridconnect.domain.repository.TransactionRepository
import com.example.hybridconnect.domain.services.interfaces.AppControl
import com.example.hybridconnect.domain.usecase.UpdateTransactionUseCase
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

    @Inject
    lateinit var updateTransactionUseCase: UpdateTransactionUseCase

    @Inject
    lateinit var appControl: AppControl

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
            if (appControl.appState.value != AppState.STATE_RUNNING) {
                break
            }

            val transaction = transactionRepository.transactionQueue.poll() ?: break
            val apps = connectedAppRepository.getConnectedApps().first()
            val activeApps = apps.filter { it.isOnline }

            if (activeApps.isEmpty()) {
                Log.e(TAG, "No connected apps to process transactions")
                delay(1000)
                continue
            }

            try {
                sendWebsocketMessage(transaction)
            } catch (e: UnavailableOfferException) {
                Log.e(TAG, e.message.toString())
            } catch (e: Exception) {
                Log.e(TAG, "Transaction ${transaction.id} failed, retrying later.")
                transactionRepository.transactionQueue.add(transaction)
                delay(2000)
            }
        }

        stopSelf()
    }


    private suspend fun sendWebsocketMessage(transaction: Transaction) {
        val apps = connectedAppRepository.getConnectedApps().first()
        val activeApps = apps.filter { it.isOnline }

        if (activeApps.isEmpty()) {
            throw Exception("No connected apps available to process the message")
        }

        var nextIndex = 0
        val selectedApp = if (transaction.offer == null) {
            activeApps.first()
        } else {
            val offer = transaction.offer
            val appsByOffer = connectedAppRepository.getAppsByOffer(offer)
            val activeAppsByOffer = appsByOffer.filter { it.isOnline }

            if (activeAppsByOffer.isEmpty()) {
                throw Exception("App for the offer exists but is offline")
            } else {
                val lastUsedIndex = connectedAppRepository.getLastUsedIndexForOffer(offer.id)
                nextIndex = (lastUsedIndex + 1) % activeAppsByOffer.size
                activeAppsByOffer[nextIndex]
            }
        }

        socketService.sendMessageToApp(selectedApp, transaction.mpesaMessage)
        updateTransactionUseCase(transaction.copy(app = selectedApp))

        // Update round-robin index per offer
        transaction.offer?.let { offer ->
            connectedAppRepository.setLastUsedIndexForOffer(offer.id, nextIndex)
        }
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