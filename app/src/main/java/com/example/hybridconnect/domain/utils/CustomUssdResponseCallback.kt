package com.example.hybridconnect.domain.utils

import android.annotation.SuppressLint
import android.telephony.TelephonyManager
import com.example.hybridconnect.domain.model.Transaction

@SuppressLint("NewApi")
class CustomUssdResponseCallback(
    private val transaction: Transaction,
    private val onSuccess: (transaction: Transaction, response: String) -> Unit,
    private val onFailure: (transaction: Transaction, failureCode: Int, response: String) -> Unit,
) : TelephonyManager.UssdResponseCallback() {

    companion object {
        val FAILURE_REGEX = Regex(
            "(?i)\\b(sorry|error|failed|fail|not available|invalid|denied|unavailable|unsupported|try again|does not exist|timeout|not recognized|insufficient|blocked|restricted|forbidden|service not reachable|balance too low|expired|network problem|cancelled|connection problem|terminated|invalid input|unregistered|unsuccessful|Max Number of Menu Retries|Duplicate sessions)\\b"
        )
    }

    override fun onReceiveUssdResponse(
        telephonyManager: TelephonyManager,
        request: String,
        response: CharSequence,
    ) {
        val responseText = response.toString()
        if (FAILURE_REGEX.containsMatchIn(responseText)) {
            onFailure(transaction, -1, responseText)
        } else {
            onSuccess(transaction, responseText)
        }
    }

    override fun onReceiveUssdResponseFailed(
        telephonyManager: TelephonyManager,
        request: String,
        failureCode: Int,
    ) {
        onFailure(transaction, failureCode, "Connection problem or invalid MMI code")
    }
}