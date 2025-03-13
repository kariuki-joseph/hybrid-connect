package com.example.hybridconnect.domain.usecase

import android.content.Context
import android.net.Uri
import com.example.hybridconnect.domain.model.RawSmsMessage

class ReadMpesaMessagesUseCase(
    private val context: Context,
    private val subscriptionIdFetcherUseCase: SubscriptionIdFetcherUseCase,
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
            "date DESC LIMIT $bufferSize"
        )

        cursor?.use {
            val bodyIndex = it.getColumnIndex("body")
            val senderIndex = it.getColumnIndex("address")
            val dateIndex = it.getColumnIndex("date")
            val simIndex = it.getColumnIndex("sim_id")
            if (bodyIndex != -1 && senderIndex != -1 && dateIndex != -1) {
                while (cursor.moveToNext()) {
                    val body = cursor.getString(bodyIndex)
                    val sender = cursor.getString(senderIndex)
                    val timeStamp = cursor.getLong(dateIndex)
                    val simSubId = cursor.getInt(simIndex)
                    val simSlot = subscriptionIdFetcherUseCase.getSlotFromSubId(simSubId)
                        .takeIf { slot -> slot != -1 }

                    messagesList.add(
                        RawSmsMessage(
                            message = body,
                            sender = sender,
                            simSlot = simSlot,
                            timestamp = timeStamp
                        )
                    )
                }
            }
        }

        return messagesList
    }
}