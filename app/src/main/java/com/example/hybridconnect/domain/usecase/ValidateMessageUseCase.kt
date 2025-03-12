package com.example.hybridconnect.domain.usecase

import android.util.Log
import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.enums.SmsType
import com.example.hybridconnect.domain.exception.InvalidMessageFormatException
import com.example.hybridconnect.domain.exception.InvalidSenderException
import com.example.hybridconnect.domain.exception.InvalidSubscriptionId
import com.example.hybridconnect.domain.exception.RecommendationTimedOutException
import com.example.hybridconnect.domain.repository.SettingsRepository
import com.example.hybridconnect.domain.utils.formatPhoneToTenDigits
import com.example.hybridconnect.domain.utils.getSmsType
import javax.inject.Inject

private const val TAG = "ValidateMessageUseCase"

class ValidateMessageUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(message: String, sender: String, simSlot: Int) {
        Log.d(TAG, "Params to validate: Sender: $sender, Message: $message, SimSlot: $simSlot")
        validateEnabledSimCards(simSlot)
        checkValidSender(sender, message)
        checkRecommendationTimeoutMessage(message)
        checkValidMessage(message)
    }

    private fun checkValidMessage(message: String) {
        // Updated regex to validate the message format for the three cases
        val regex = Regex(
            pattern = "(^BHSL)|((received from \\d{9,12})|(received Ksh\\d+(\\.\\d{2})?))",
            options = setOf(RegexOption.IGNORE_CASE)
        )
        if (regex.find(message) == null) {
            throw InvalidMessageFormatException("Invalid message format: $message")
        }
    }

    private fun checkRecommendationTimeoutMessage(message: String) {
        val recommendationTimeoutRegex = Regex(
            pattern = "Recommendation for (\\d{9,12}) timed out",
            options = setOf(RegexOption.IGNORE_CASE)
        )
        val match = recommendationTimeoutRegex.find(message)
        if (match != null) {
            throw RecommendationTimedOutException(message)
        }
    }


    private suspend fun checkValidSender(sender: String, message: String) {
        if (sender.isEmpty()) {
            throw InvalidSenderException("Sender cannot be blank")
        }

        val smsType = getSmsType(message)

        if (sender != "MPESA" && sender != "Safaricom" && smsType != SmsType.SITE_LINK) {
            throw InvalidSenderException("Not allowed to process messages from sender $sender")
        }

        if (smsType == SmsType.SITE_LINK) {
            val siteLinkNumber = settingsRepository.getSetting(AppSetting.ADMIN_SITE_LINK_NUMBER)
            val formattedSender = formatPhoneToTenDigits(sender)
            if (formattedSender != siteLinkNumber) {
                throw Exception("App is only allowed to process SiteLink messages from $siteLinkNumber")
            }
        }

        if(message.contains("MWIRIGI AIRTIME SPOT", ignoreCase = true)){
            throw Exception("App not allowed to process messages from admin")
        }
    }

    private suspend fun validateEnabledSimCards(slot: Int) {
        if (slot < 0) {
            throw InvalidSubscriptionId("Invalid SIM Slot : $slot")
        }
        val allowedSimSlots = mutableSetOf<Int>()
        if (settingsRepository.getSetting(AppSetting.RECEIVE_PAYMENTS_VIA_SIM_1).toBoolean()) {
            allowedSimSlots.add(0)
        }

        if (settingsRepository.getSetting(AppSetting.RECEIVE_PAYMENTS_VIA_SIM_2).toBoolean()) {
            allowedSimSlots.add(1)
        }

        if (!allowedSimSlots.contains(slot)) {
            throw Exception("Not allowed to process messages from SIM ${slot + 1}")
        }
    }
}