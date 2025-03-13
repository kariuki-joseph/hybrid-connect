package com.example.hybridconnect.domain.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.hybridconnect.domain.enums.TransactionStatus

@Composable
fun getColorForTransactionStatus(transactionStatus: TransactionStatus): Color {
    return when (transactionStatus) {
        TransactionStatus.OK -> Color.Green
        TransactionStatus.UNMATCHED -> MaterialTheme.colorScheme.secondary
    }
}