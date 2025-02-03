package com.example.hybridconnect.domain.services

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.telephony.SubscriptionManager
import com.example.hybridconnect.domain.repository.PrefsRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SmsReceiver : BroadcastReceiver() {
    @Inject
    lateinit var prefsRepository: PrefsRepository

    private val messageParts = mutableMapOf<String, StringBuilder>()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            decodeSms(context, intent)
        }
    }

    private fun decodeSms(context: Context, intent: Intent) {
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val pdus = bundle["pdus"] as? Array<*>
            if (pdus != null) {
                val subscriptionId = bundle.getInt("subscription", -1)
                val simSlot = getSimSlotForSubscription(context, subscriptionId)

                // Extract sender information from the first PDU
                val smsMessage = SmsMessage.createFromPdu(pdus[0] as ByteArray)
                val sender = smsMessage.displayOriginatingAddress
                val timestamp = smsMessage.timestampMillis
                val messageId = "$sender-$timestamp" // Unique key based on sender and timestamp

                // Initialize StringBuilder for this messageId if not already present
                if (!messageParts.containsKey(messageId)) {
                    messageParts[messageId] = StringBuilder()
                }

                // Iterate through all PDUs and append the message bodies
                for (i in pdus.indices) {
                    val partMessage = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                    val messageBody = partMessage.displayMessageBody

                    // Append message body to the StringBuilder
                    messageParts[messageId]?.append(messageBody)
                }

                // After processing all parts, send the complete message
                val fullMessage = messageParts[messageId].toString()

                // Clean up the message parts map for this messageId
                messageParts.remove(messageId)
                if (prefsRepository.isAppActive()) {
                    // start foreground service with SMS data
                    val serviceIntent = Intent(context, SmsProcessingService::class.java).apply {
                        putExtra("message", fullMessage)
                        putExtra("sender", sender)
                        putExtra("simSlot", simSlot)
                    }
                    context.startForegroundService(serviceIntent)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getSimSlotForSubscription(context: Context, subscriptionId: Int): Int {
        if (subscriptionId == -1) {
            return -1 // Unknown sim slot
        }
        val subscriptionManager =
            context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        val subscriptionInfo = subscriptionManager.getActiveSubscriptionInfo(subscriptionId)
        return subscriptionInfo?.simSlotIndex ?: -1
    }
}