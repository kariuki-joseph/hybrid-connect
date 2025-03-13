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
import com.example.hybridconnect.domain.usecase.ReadMpesaMessagesUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SmsProcessingService"

@AndroidEntryPoint
class SmsProcessingService : Service() {
    companion object {
        const val CHANNEL_ID = "HybridConnectServiceChannel"
    }

    @Inject
    lateinit var smsProcessor: SmsProcessor

    @Inject
    lateinit var readMpesaMessagesUseCase: ReadMpesaMessagesUseCase

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotification("Waiting", "Waiting for new sms")

        intent?.getStringExtra("message")?.let { message ->
            val sender = intent.getStringExtra("sender") ?: ""
            val simSlot = intent.getIntExtra("simSlot", -1)
            showNotification("Processing", "Processing SMS from $sender")
            serviceScope.launch {
                Log.d(TAG, "Processing sms request inside a service, $message , $sender, $simSlot")
                smsProcessor.processMessage(message, sender, simSlot)

                // dry-run M-Pesa messages to make sure none is skipped
                readMpesaMessagesUseCase().forEach { msg ->
                    smsProcessor.processMessage(msg.message, msg.sender, msg.simSlot)
                }

                showNotification("Waiting", "Waiting for new sms")
            }
        }

        return START_STICKY
    }

    private fun showNotification(title: String, content: String) {
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.logo)
            .build()
        startForeground(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Hybrid Connect SMS Listening Service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }
}