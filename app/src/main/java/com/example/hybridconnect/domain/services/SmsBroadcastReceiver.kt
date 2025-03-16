package com.example.hybridconnect.domain.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
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
                val simSlot = subscriptionIdFetcherUseCase.getSlotFromSubId(subscriptionId)

                val smsMessage = SmsMessage.createFromPdu(pdus[0] as ByteArray)
                val sender = smsMessage.displayOriginatingAddress
                val timestamp = smsMessage.timestampMillis
                val messageId = "$sender-$timestamp"

                if (!messageParts.containsKey(messageId)) {
                    messageParts[messageId] = StringBuilder()
                }

                for (i in pdus.indices) {
                    val partMessage = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                    val messageBody = partMessage.displayMessageBody
                    messageParts[messageId]?.append(messageBody)
                }

                val fullMessage = messageParts[messageId].toString()

                messageParts.remove(messageId)

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