package com.example.hybridconnect.presentation.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hybridconnect.domain.enums.OfferType
import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.model.Customer
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.utils.formatTimeAgo
import com.example.hybridconnect.domain.utils.getColorForTransactionStatus
import com.example.hybridconnect.domain.utils.getIconForTransactionStatus
import com.example.hybridconnect.domain.utils.isAlreadyRecommendedResponse
import java.util.UUID

@Composable
fun TransactionItem(
    transaction: Transaction,
    modifier: Modifier = Modifier,
    onClick: (Transaction) -> Unit,
    onRetry: (Transaction) -> Unit = {},
) {
    Log.d("TransactionItem", "TransactionItem: $transaction")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .wrapContentHeight()
    ) {
        Column {
            val icon = getIconForTransactionStatus(transaction.status, transaction.responseMessage)
            val iconColor =  getColorForTransactionStatus(transaction.status)

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

        }
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { onClick(transaction) }
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = transaction.customer.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = formatTimeAgo(transaction.time),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.secondary
                    ),
                )
            }
            Spacer(modifier = Modifier.height(.5.dp))
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = transaction.offer?.name ?: "Unavailable",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = "Ksh. ${transaction.amount}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionItemPreview() {
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
            id = 1, name = "John Doe", phone = "012345678", accountBalance = 200
        ),
        time = System.currentTimeMillis(),
        message = "Confirmed You have received",
        responseMessage = "Request Submitted Successfully",
            status = TransactionStatus.UNMATCHED,
        offer = offer
    )

    TransactionItem(transaction = transaction, onClick = {})
}