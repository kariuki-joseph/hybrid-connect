package com.example.hybridconnect.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hybridconnect.domain.enums.OfferType
import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.model.Customer
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.utils.formatDateTime
import com.example.hybridconnect.domain.utils.getColorForTransactionStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    transaction: Transaction,
    onRetry: (transaction: Transaction) -> Unit = {},
    isRetrying: Boolean,
    onReschedule: (transaction: Transaction) -> Unit = {},
    onDelete: (transaction: Transaction) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = transaction.customer.phone,
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .size(24.dp),
                    onClick = {
                        clipboardManager.setText(AnnotatedString(transaction.customer.phone))
                        isCopied = true
                        scope.launch {
                            delay(2000)
                            isCopied = false
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isCopied) Icons.Filled.Check else Icons.Default.ContentCopy,
                        contentDescription = if (isCopied) "Copied" else "Copy",
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = formatDateTime(transaction.time),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    transaction.offer?.let { offer ->
                        Text(text = "${offer.name} ", style = MaterialTheme.typography.bodySmall)
                        Text(
                            text = "(${offer.ussdCode})",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Text(
                    text = "Ksh. ${transaction.amount}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight(700)
                    )
                )
            }

            if (transaction.status != TransactionStatus.UNMATCHED) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Response",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = transaction.responseMessage,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = getColorForTransactionStatus(transaction.status)
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Mpesa Message",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = transaction.mpesaMessage,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                ActionButtons(
                    transactionStatus = transaction.status,
                    isRetrying = isRetrying,
                    onDelete = { onDelete(transaction) },
                    onReschedule = { onReschedule(transaction) },
                    onRetry = { onRetry(transaction) }
                )
            }
        }
    }
}

@Composable
fun ActionButtons(
    transactionStatus: TransactionStatus,
    isRetrying: Boolean,
    onDelete: () -> Unit = {},
    onReschedule: () -> Unit = {},
    onRetry: () -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
            Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete")
        }
        IconButton(onClick = onReschedule, modifier = Modifier.size(24.dp)) {
            Icon(imageVector = Icons.Default.Schedule, contentDescription = "Reschedule")
        }
        if (isRetrying) {
            IconButton(onClick = {}, modifier = Modifier.size(24.dp)) {
                CircularProgressIndicator()
            }
        } else {
            IconButton(
                onClick = onRetry,
                modifier = Modifier.size(24.dp),
                enabled = transactionStatus == TransactionStatus.FAILED
            ) {
                Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Retry")
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
private fun InfoCardPreview() {
    val offer = Offer(
        id = UUID.randomUUID(),
        name = "2GB for 2 Hours",
        ussdCode = "*188*2*1*BH*1#",
        price = 30,
        type = OfferType.DATA
    )
    val transaction = Transaction(
        id = UUID.randomUUID(),
        amount = 20,
        customer = Customer(
            id = 1, name = "John Doe", phone = "012345678", accountBalance = 200
        ),
        time = System.currentTimeMillis(),
        mpesaMessage = "Confirmed You have received",
        responseMessage = "Request Submitted Successfully",
        status = TransactionStatus.UNMATCHED,
        offer = offer,
    )

    InfoCard(
        transaction = transaction,
        isRetrying = false,
        onRetry = {},
        onReschedule = {},
        onDelete = {})
}