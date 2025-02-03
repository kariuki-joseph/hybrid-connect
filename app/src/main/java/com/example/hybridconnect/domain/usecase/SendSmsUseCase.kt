package com.example.hybridconnect.domain.usecase

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.hybridconnect.MainActivity
import javax.inject.Inject

private const val TAG = "SendSmsUseCase"
class SendSmsUseCase @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val SEND_SMS_REQUEST_CODE = 1;
    }

    operator fun invoke(receiverPhone: String, message: String){
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.SEND_SMS
                )
            ) {
                Toast.makeText(
                    context,
                    "Please allow this app to send messages in app permissions.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                ActivityCompat.requestPermissions(context,arrayOf(Manifest.permission.SEND_SMS),
                    SEND_SMS_REQUEST_CODE
                )
            }
            return
        }
        // Permission has been granted. Send message here
        val smsManager: SmsManager = context.getSystemService(SmsManager::class.java)

        // For long messages, use sendMultipartTextMessage
        if (message.length > 160) {
            val messageParts = smsManager.divideMessage(message)
            smsManager.sendMultipartTextMessage(receiverPhone, null, messageParts, null, null)
            Log.d(TAG, "Sent multipart message to $receiverPhone")
        } else {
            smsManager.sendTextMessage(receiverPhone, null, message, null, null)
            Log.d(TAG, "Sent message to $receiverPhone")
        }
        
    }
}