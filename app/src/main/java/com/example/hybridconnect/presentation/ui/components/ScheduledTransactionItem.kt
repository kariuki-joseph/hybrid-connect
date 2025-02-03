package com.example.hybridconnect.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.hybridconnect.domain.utils.getIconForTransactionStatus
import com.example.hybridconnect.domain.utils.isAlreadyRecommendedResponse
import java.util.UUID

@Composable
fun ScheduledTransactionItem(
    modifier: Modifier = Modifier,
    transaction: Transaction,
    onClick: (Transaction) -> Unit,
    onRetry: (Transaction) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        val icon = getIconForTransactionStatus(transaction.status, transaction.responseMessage)
        val iconColor = getColorForTransactionStatus(transaction.status)
        IconButton(
            onClick = {
                if (transaction.status == TransactionStatus.FAILED && !isAlreadyRecommendedResponse(transaction.responseMessage)) {
                    onRetry(transaction)
                }
            },
            modifier = Modifier
                .size(48.dp)
                .padding(end = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor
            )
        }

        Column(
            modifier = Modifier
                .clickable { onClick(transaction) }
                .padding(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = transaction.customer.name,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                )

                Text(
                    text = formatDateTime(transaction.time),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    ),
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${transaction.offer?.name}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(end = 8.dp)
                )

                Text(
                    text = "Ksh. ${transaction.amount}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScheduledTransactionItemPreview() {
    val offer = Offer(
        id = UUID.randomUUID(),
        name = "2GB for 2 Hours and a very long text",
        ussdCode = "*188*2*1*BH*1#",
        price = 30,
        type = OfferType.DATA
    )
    val transaction = Transaction(
        id = UUID.randomUUID(),
        amount = 20,
        customer = Customer(
            id = 1,
            name = "John Doe",
            phone = "012345678",
            accountBalance = 200
        ),
        time = System.currentTimeMillis(),
        mpesaMessage = "Confirmed You have received",
        responseMessage = "you have been already recommended",
        status = TransactionStatus.FAILED,
        offer = offer
    )

    ScheduledTransactionItem(
        transaction = transaction,
        onClick = {},
        onRetry = {}
    )
}