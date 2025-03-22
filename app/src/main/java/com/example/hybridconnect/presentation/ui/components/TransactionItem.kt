package com.example.hybridconnect.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.hybridconnect.domain.model.Transaction

@Composable
fun TransactionItem(
    transaction: Transaction,
    modifier: Modifier = Modifier,
    onClick: (Transaction) -> Unit,
    onRetry: (Transaction) -> Unit = {},
) {
}