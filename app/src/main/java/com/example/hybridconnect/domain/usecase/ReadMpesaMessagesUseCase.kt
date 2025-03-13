package com.example.hybridconnect.domain.usecase

import android.content.Context
import android.net.Uri
import com.example.hybridconnect.domain.model.RawSmsMessage

class ReadMpesaMessagesUseCase(
    private val context: Context,
    private val subscriptionIdFetcherUseCase: SubscriptionIdFetcherUseCase
) {

    operator fun invoke(bufferSize: Int = 10): List<RawSmsMessage> {
        val uri = Uri.parse("content://sms/inbox")
        val projection = arrayOf("body", "address", "date", "sim_slot_index")
        val selection = "address = ?" // Exact match for MPESA
        val selectionArgs = arrayOf("MPESA")

        val messagesList = mutableListOf<RawSmsMessage>()

        val cursor = context.contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            "date DESC"
        )

        cursor?.use {
            val bodyIndex = it.getColumnIndex("body")
            val senderIndex = it.getColumnIndex("address")
            val dateIndex = it.getColumnIndex("date")
            val simIndex = it.getColumnIndex("sim_id")
            if (bodyIndex != -1 && senderIndex != -1 && dateIndex != -1) {
                var count = 0
                while (it.moveToNext() && count < bufferSize) {
                    val body = it.getString(bodyIndex)
                    val sender = it.getString(senderIndex)
                    val timeStamp = it.getLong(dateIndex)
                    val simSubId = cursor.getInt(simIndex)
                    val simSlot = subscriptionIdFetcherUseCase.getSlotFromSubId(simSubId).takeIf { slot -> slot != -1 }

                    val rawSms = RawSmsMessage(
                        message = body,
                        sender = sender,
                        simSlot = simSlot,
                        timestamp = timeStamp,
                    )
                    messagesList.add(rawSms)
                    count++
                }
            }
        }

        return messagesList
    }
}