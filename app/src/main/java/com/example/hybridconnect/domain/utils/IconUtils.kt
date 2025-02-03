package com.example.hybridconnect.domain.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.hybridconnect.domain.enums.OfferType
import com.example.hybridconnect.domain.enums.TransactionStatus

fun getIconForTransactionStatus(status: TransactionStatus, responseMessage: String): ImageVector {
    return when (status) {
        TransactionStatus.SCHEDULED -> Icons.Filled.Schedule
        TransactionStatus.PROCESSING -> Icons.Filled.HourglassEmpty
        TransactionStatus.SUCCESS -> Icons.Filled.Check
        TransactionStatus.FAILED -> if (isAlreadyRecommendedResponse(responseMessage)) Icons.Outlined.Close else Icons.Filled.Refresh
        TransactionStatus.UNMATCHED -> Icons.Filled.Info
        TransactionStatus.RESCHEDULED -> Icons.Filled.Schedule
    }
}

fun getIconForOfferType(offerType: OfferType): ImageVector {
    return when (offerType) {
        OfferType.DATA -> Icons.Outlined.Language
        OfferType.VOICE -> Icons.Outlined.Phone
        OfferType.SMS -> Icons.AutoMirrored.Outlined.Message
        OfferType.NONE -> Icons.Outlined.Error
    }
}