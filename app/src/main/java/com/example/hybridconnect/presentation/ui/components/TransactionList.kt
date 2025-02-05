package com.example.hybridconnect.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hybridconnect.domain.enums.OfferType
import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.model.Customer
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.model.Transaction
import java.util.UUID

@Composable
fun TransactionList(
    autoScroll: Boolean = true,
    transactions: List<Transaction>,
    onTransactionClick: (Transaction) -> Unit = {},
    onRetry: (Transaction) -> Unit = {},
) {
    val listState = rememberLazyListState()

    val firstItemVisible by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0
        }
    }

    LaunchedEffect(transactions.size) {
        if(transactions.isNotEmpty() && autoScroll){
            listState.scrollToItem(0)
        }
    }

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(0.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(transactions, key = { it.id.toString() }) { transaction ->
            Column {
                TransactionItem(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction) },
                    onRetry = onRetry
                )
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    thickness = 1.dp
                )
            }

        }

        item {
            Spacer(modifier = Modifier.height(84.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionListPreview() {
    val mockTransactions = listOf(
        Transaction(
            id = UUID.randomUUID(),
            amount = 500,
            message = "Payment received for Order #123",
            responseMessage = "Transaction successful",
            status = TransactionStatus.FAILED,
            time = System.currentTimeMillis(),
            customer = Customer(
                id = 1,
                name = "John Doe",
                phone = "0712345678",
                accountBalance = 1000
            ),
            offer = Offer(
                id = UUID.randomUUID(),
                name = "Discount 20%",
                ussdCode = "*123#",
                price = 100,
                type = OfferType.DATA,
                isSiteLinked = false
            ),
        ),
        Transaction(
            id = UUID.randomUUID(),
            amount = 1000,
            message = "Payment received for Order #124",
            responseMessage = "Transaction successful",
            status = TransactionStatus.SUCCESS,
            time = System.currentTimeMillis(),
            customer = Customer(
                id = 2,
                name = "Jane Doe",
                phone = "0712345679",
                accountBalance = 1500
            ),
            offer = Offer(
                id = UUID.randomUUID(),
                name = "Cashback Offer",
                ussdCode = "*456#",
                price = 200,
                type = OfferType.DATA,
                isSiteLinked = true
            )
        ),
        Transaction(
            id = UUID.randomUUID(),
            amount = 750,
            message = "Payment for electricity bill",
            responseMessage = "Transaction successful",
            status = TransactionStatus.SUCCESS,
            time = System.currentTimeMillis(),
            customer = Customer(
                id = 3,
                name = "Alex Smith",
                phone = "0723456789",
                accountBalance = 500
            ),
            offer = null
        ),
        Transaction(
            id = UUID.randomUUID(),
            amount = 200,
            message = "Airtime purchase",
            responseMessage = "Transaction successful",
            status = TransactionStatus.SUCCESS,
            time = System.currentTimeMillis(),
            customer = Customer(
                id = 4,
                name = "Mary Johnson",
                phone = "0734567890",
                accountBalance = 300
            ),
            offer = Offer(
                id = UUID.randomUUID(),
                name = "Top-up Bonus",
                ussdCode = "*789#",
                price = 50,
                type = OfferType.SMS,
                isSiteLinked = false
            )
        ),
        Transaction(
            id = UUID.randomUUID(),
            amount = 1500,
            message = "Payment for Order #125",
            responseMessage = "Transaction scheduled",
            status = TransactionStatus.SCHEDULED,
            time = System.currentTimeMillis(),
            customer = Customer(
                id = 5,
                name = "Chris Evans",
                phone = "0745678901",
                accountBalance = 2000
            ),
            offer = Offer(
                id = UUID.randomUUID(),
                name = "Free Shipping",
                ussdCode = "*101#",
                price = 0,
                type = OfferType.VOICE,
                isSiteLinked = true
            )
        )
    )

    TransactionList(transactions = mockTransactions)
}
