package com.example.hybridconnect.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.domain.enums.OfferType
import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.enums.RescheduleMode
import com.example.hybridconnect.domain.model.Customer
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.model.RescheduleInfo
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.utils.SnackbarManager
import com.example.hybridconnect.domain.utils.formatDateTime
import com.example.hybridconnect.presentation.navigation.Route
import com.example.hybridconnect.presentation.ui.components.InfoCard
import com.example.hybridconnect.presentation.ui.components.ScheduledTransactionItem
import com.example.hybridconnect.presentation.viewmodel.TransactionDetailsViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun TransactionDetailsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: TransactionDetailsViewModel = hiltViewModel(),
    onBackClick: () -> Unit = { navController.popBackStack() },
    transactionId: UUID,
) {
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val onDeleteSuccess by viewModel.onDeleteSuccess.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val transaction by viewModel.transaction.collectAsState()
    val scheduledTransactions by viewModel.scheduledTransactions.collectAsState()
    val isRetrying by viewModel.isRetrying.collectAsState()

    LaunchedEffect(transactionId) {
        viewModel.getTransaction(transactionId)
    }

    LaunchedEffect(onDeleteSuccess) {
        if (onDeleteSuccess) {
            navController.popBackStack()
        }
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            SnackbarManager.showMessage(scope, it)
            viewModel.resetSnackbarMessage()
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Transaction Details") }, navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        })
    }) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            transaction?.let { transaction ->
                TransactionDetailsScreenContent(
                    transaction = transaction,
                    scheduledTransactions = scheduledTransactions,
                    isRetrying = isRetrying,
                    onRetry = { trans -> viewModel.retryTransaction(trans) },
                    onDelete = { showDeleteDialog = true },
                    onReschedule = {
                        navController.navigate(
                            Route.RescheduleOffer.name.replace(
                                "{transactionId}",
                                transactionId.toString()
                            )
                        )
                    },
                    onViewTransaction = {
                        navController.navigate("transactions/${it.id}")
                    },
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete this transaction?") },
            confirmButton = {
                OutlinedButton(onClick = {
                    viewModel.deleteTransaction()
                    showDeleteDialog = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            })
    }
}

@Composable
fun TransactionDetailsScreenContent(
    transaction: Transaction,
    scheduledTransactions: List<Transaction>,
    isRetrying: Boolean,
    onRetry: (transaction: Transaction) -> Unit,
    onDelete: () -> Unit,
    onReschedule: (Transaction) -> Unit,
    onViewTransaction: (Transaction) -> Unit,
) {
    Column {
        InfoCard(
            transaction = transaction,
            isRetrying = isRetrying,
            onRetry = { onRetry(it) },
            onDelete = { onDelete() },
            onReschedule = { onReschedule(transaction) }
        )

        transaction.rescheduleInfo?.let { rescheduleInfo ->
            if (transaction.status == TransactionStatus.RESCHEDULED && rescheduleInfo.rescheduleMode == RescheduleMode.ONCE) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = buildAnnotatedString {
                    append("Rescheduled to ")
                    withStyle(
                        style = SpanStyle(
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    ) {
                        append(transaction.rescheduleInfo.time.let { formatDateTime(it) })
                    }
                })
            }
        }

        if (scheduledTransactions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Rescheduled To")
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(scheduledTransactions) { transaction ->
                    ScheduledTransactionItem(
                        transaction = transaction,
                        onClick = { onViewTransaction(it) },
                        onRetry = { onRetry(it) })
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionDetailsScreenPreview() {
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
            id = 1,
            name = "John Doe",
            phone = "012345678",
            accountBalance = 200
        ),
        time = System.currentTimeMillis(),
        mpesaMessage = "Confirmed You have received",
        responseMessage = "Request Submitted Successfully",
        status = TransactionStatus.SCHEDULED,
        offer = offer,
        rescheduleInfo = RescheduleInfo(
            time = System.currentTimeMillis()
        )
    )

    TransactionDetailsScreenContent(
        transaction = transaction,
        scheduledTransactions = listOf(transaction),
        isRetrying = true,
        onRetry = {},
        onDelete = {},
        onReschedule = {},
        onViewTransaction = {},
    )
}