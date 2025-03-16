package com.example.hybridconnect.domain.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.widget.Toast
import com.example.hybridconnect.domain.enums.AppState
import com.example.hybridconnect.domain.services.interfaces.AppControl
import com.example.hybridconnect.domain.usecase.SubscriptionIdFetcherUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SmsBroadcastReceiver : BroadcastReceiver() {
    @Inject
    lateinit var appControl: AppControl

    @Inject
    lateinit var subscriptionIdFetcherUseCase: SubscriptionIdFetcherUseCase

    private val messageParts = mutableMapOf<String, StringBuilder>()

    override fun onReceive(context: Context, intent: Intent) {
        if (appControl.appState.value == AppState.STATE_STOPPED) return

        when (intent.action) {
            "android.provider.Telephony.SMS_RECEIVED" -> {
                Toast.makeText(context, "SMS Broadcast Received", Toast.LENGTH_SHORT).show()
                decodeSms(context, intent)
            }

            "android.provider.Telephony.SMS_DELIVER" -> {
                Toast.makeText(context, "SMS Deliver Broadcast Received", Toast.LENGTH_SHORT).show()
            }

            "android.provider.Telephony.WAP_PUSH_DELIVER" -> {
                Toast.makeText(context, "WAP Push Broadcast Received", Toast.LENGTH_SHORT).show()
                // Handle WAP Push message if needed
            }

            else -> {
                Toast.makeText(context, "Unknown Broadcast Received", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun decodeSms(context: Context, intent: Intent) {
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val pdus = bundle["pdus"] as? Array<*>
            if (pdus != null) {
                val subscriptionId = bundle.getInt("subscription", -1)
                val simSlot = subscriptionIdFetcherUseCase.getSlotFromSubId(subscriptionId)

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

                // Cleanup
                messageParts.remove(messageId)

                Toast.makeText(context, "All done!", Toast.LENGTH_SHORT).show()
                
                val serviceIntent = Intent(context, SmsProcessingService::class.java).apply {
                    putExtra("message", fullMessage)
                    putExtra("sender", sender)
                    putExtra("simSlot", simSlot)
                }

                context.startForegroundService(serviceIntent)

            } else {
                Toast.makeText(context, "Received PDU is null", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Received Bundle is null", Toast.LENGTH_SHORT).show()
        }
    }
}