package com.example.hybridconnect.domain.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.hybridconnect.domain.enums.TransactionStatus

@Composable
fun getColorForTransactionStatus(transactionStatus: TransactionStatus): Color {
    return when (transactionStatus) {
        TransactionStatus.SCHEDULED -> MaterialTheme.colorScheme.tertiary
        TransactionStatus.PROCESSING -> Color.Yellow
        TransactionStatus.SUCCESS -> MaterialTheme.colorScheme.primary
        TransactionStatus.FAILED -> MaterialTheme.colorScheme.error
        TransactionStatus.UNMATCHED -> MaterialTheme.colorScheme.secondary
        TransactionStatus.RESCHEDULED -> MaterialTheme.colorScheme.tertiary
    }
}