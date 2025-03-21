package com.example.hybridconnect.presentation.ui.components;

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.model.ConnectedApp

@Composable
fun ConnectedAppComponent(
    connectedApp: ConnectedApp,
    transactionStatusCounts: Map<TransactionStatus, Int>,
    connectedOffersCount: Int = 0,
    onClick: () -> Unit,
) {
    val sentCount = transactionStatusCounts[TransactionStatus.SENT] ?: 0
    val receivedCount = transactionStatusCounts[TransactionStatus.RECEIVED] ?: 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildAnnotatedString {
                        append(connectedApp.connectId)
                        append(" ")
                        withStyle(
                            style = SpanStyle(
                                fontSize = 11.sp
                            )
                        ) {
                            append("(${connectedApp.appName})")
                        }
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(8.dp))
                Dot(isOnline = connectedApp.isOnline)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = connectedOffersCount.toString(),
                    style = MaterialTheme.typography.labelSmall
                )

                Text(
                    text = " Offers Added",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("$sentCount")
                        }
                        append(" --> ")
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                            )
                        ) {
                            append("$receivedCount")
                        }
                        append(" Sent")
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            }
        }
    }
}

@Composable
private fun Dot(
    isOnline: Boolean = false,
) {
    val color = if (isOnline) Color.Green else MaterialTheme.colorScheme.error
    Box(
        modifier = Modifier
            .size(10.dp)
            .background(color, CircleShape)
    )
}

@Preview(showBackground = true)
@Composable
private fun ConnectedAppComponentPreview() {
    val connectedApp = ConnectedApp(
        connectId = "BHC-6SHYS",
        appName = "Hybrid Main",
        isOnline = true,
        messagesSent = 30,
    )

    val transactionStatusCounts = mapOf<TransactionStatus, Int>(
        TransactionStatus.RECEIVED to 2
    )

    ConnectedAppComponent(
        connectedApp = connectedApp,
        transactionStatusCounts = transactionStatusCounts,
        connectedOffersCount = 30,
        onClick = {}
    )
}
