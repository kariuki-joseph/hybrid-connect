package com.example.hybridconnect.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class SmsViewModel : ViewModel() {
    private val _messages = mutableStateListOf<String>()
    val messages: List<String> get() = _messages

    fun readMPEMASMS(context: Context) {
        val uri = Uri.parse("content://sms/inbox")
        val projection = arrayOf("address", "body", "date") // Include 'date' to sort by it
        val selection = "address = ?"
        val selectionArgs = arrayOf("MPESA")

        val cursor = context.contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            "date DESC LIMIT 1500"
        )

        cursor?.use {
            while (it.moveToNext()) {
                val bodyIndex = it.getColumnIndex("body")
                if (bodyIndex != -1) {
                    val body = it.getString(bodyIndex)
                    _messages.add(body)
                }
            }
        }
    }

    fun removeMessage(message: String){
        _messages.remove(message)
    }
}
