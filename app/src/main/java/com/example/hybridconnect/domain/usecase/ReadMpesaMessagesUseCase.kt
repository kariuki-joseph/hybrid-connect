package com.example.hybridconnect.domain.usecase

import android.content.Context
import android.net.Uri

class ReadMpesaMessagesUseCase(
    private val context: Context
) {

    operator fun invoke(bufferSize: Int = 10): List<String> {
        val uri = Uri.parse("content://sms/inbox")
        val projection = arrayOf("body", "date")
        val selection = "address = ?" // Exact match for MPESA
        val selectionArgs = arrayOf("MPESA")

        val messagesList = mutableListOf<String>()

        val cursor = context.contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            "date DESC"
        )

        cursor?.use {
            val bodyIndex = it.getColumnIndex("body")
            if (bodyIndex != -1) {
                var count = 0
                while (it.moveToNext() && count < bufferSize) {
                    val body = it.getString(bodyIndex)
                    messagesList.add(body)
                    count++
                }
            }
        }

        return messagesList
    }
}