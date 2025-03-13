package com.example.hybridconnect.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.utils.getColorForTransactionStatus

@Composable
private fun Dot(
    transactionStatus: TransactionStatus = TransactionStatus.OK
    ) {
    val color = getColorForTransactionStatus(transactionStatus)
    Box(
        modifier = Modifier
            .size(8.dp)
            .background(color, CircleShape)
    )
}

@Preview()
@Composable
fun DotPreview() {
    Dot()
}