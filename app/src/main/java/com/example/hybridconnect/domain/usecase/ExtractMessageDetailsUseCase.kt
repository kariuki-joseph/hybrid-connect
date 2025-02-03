package com.example.hybridconnect.domain.usecase

import android.util.Log
import com.example.hybridconnect.domain.enums.SmsType
import com.example.hybridconnect.domain.model.SmsMessage
import com.example.hybridconnect.domain.services.interfaces.MessageExtractor
import com.example.hybridconnect.domain.utils.getSmsType
import javax.inject.Inject
import javax.inject.Named

private const val TAG = "ExtractMessageUseCase"

class ExtractMessageDetailsUseCase @Inject constructor(
    @Named("mpesaMessageExtractor") private val messageExtractor: MessageExtractor,
    @Named("tillMessageExtractor") private val tillMessageExtractor: MessageExtractor,
    @Named("siteLinkMessageExtractor") private val siteLinkMessageExtractor: MessageExtractor,
) {
    operator fun invoke(message: String): SmsMessage {
        return when (getSmsType(message)) {
            SmsType.TILL -> {
                Log.d(TAG, "Extracting as Till message")
                tillMessageExtractor.extractDetails(message)
            }

            SmsType.MPESA -> {
                Log.d(TAG, "Extracting as Mpesa message")
                messageExtractor.extractDetails(message)
            }

            SmsType.SITE_LINK -> {
                Log.d(TAG, "Extracting as SiteLink message")
                siteLinkMessageExtractor.extractDetails(message)
            }

            SmsType.RECOMMENDATION_TIMEOUT -> {
                Log.d(TAG, "Extracting as Recommendation Timeout message")
                tillMessageExtractor.extractDetails(message)
            }
        }
    }
}
