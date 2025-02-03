package com.example.hybridconnect.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.domain.enums.SubscriptionType
import com.example.hybridconnect.domain.utils.SnackbarManager
import com.example.hybridconnect.presentation.enums.StatisticsBoxType
import com.example.hybridconnect.presentation.navigation.Route
import com.example.hybridconnect.presentation.ui.components.StatisticsBox
import com.example.hybridconnect.presentation.ui.components.StatisticsGraph
import com.example.hybridconnect.presentation.ui.components.TransactionList
import com.example.hybridconnect.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val transactions by viewModel.transactions.collectAsState()
    val agentFirstName by viewModel.agentFirstName.collectAsState()
    val greetings by viewModel.greetings.collectAsState()
    val successCount by viewModel.successCount.collectAsState()
    val failedCount by viewModel.failedCount.collectAsState()
    val isAppActive by viewModel.isAppActive.collectAsState()
    val subscriptionLimit by viewModel.subscriptionLimit.collectAsState()
    val activeSubscriptionType by viewModel.activeSubscriptionType.collectAsState()
    val agentCommissions by viewModel.agentCommissions.collectAsState()

    val remainingTime = remember(subscriptionLimit, activeSubscriptionType) {
        when (activeSubscriptionType) {
            SubscriptionType.UNLIMITED -> {
                if (subscriptionLimit > 0) {
                    val totalHours = subscriptionLimit / (1000 * 60 * 60)
                    val minutes = (subscriptionLimit / (1000 * 60)) % 60

                    if (totalHours >= 24) {
                        val days = totalHours / 24
                        val hours = totalHours % 24
                        String.format(Locale.getDefault(), "%dd %02dh", days, hours)
                    } else {
                        String.format(Locale.getDefault(), "%02dh %02dmin", totalHours, minutes)
                    }
                } else {
                    "Expired"
                }
            }

            SubscriptionType.LIMITED -> subscriptionLimit.toString()
            else -> "Expired"
        }
    }


    var showStopAppWarningDialog by remember { mutableStateOf(false) }
    val logoutSuccess by viewModel.logoutSuccess.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    LaunchedEffect(logoutSuccess) {
        if (logoutSuccess) {
            SnackbarManager.showMessage(scope, "Logout Success")
            withContext(Dispatchers.Main) {
                navController.navigate(Route.Login.name) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            SnackbarManager.showMessage(scope, it)
            viewModel.resetSnackbarMessage()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        DrawerScaffoldScreen(
            navController = navController,
            topBarTitle = "$greetings, $agentFirstName",
            onLogout = { viewModel.logoutUser() }
        ) {
            Column(
                modifier = modifier
                    .fillMaxHeight()
            ) {
                Row(
                    modifier = modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        28.dp,
                        Alignment.CenterHorizontally
                    ),
                ) {
                    StatisticsBox(
                        label = "Successful",
                        value = successCount.toString(),
                        boxType = StatisticsBoxType.SUCCESSFUL
                    )
                    StatisticsBox(
                        label = "Failed",
                        value = failedCount.toString(),
                        boxType = StatisticsBoxType.FAILED
                    )

                    StatisticsBox(
                        label = "Tokens",
                        value = remainingTime,
                        boxType = StatisticsBoxType.TOKENS,
                        onClick = {
                            navController.navigate(Route.MySubscription.name)
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(horizontal = 15.dp, vertical = 8.dp)
                        .fillMaxWidth()
                        .height(150.dp),
                ) {
                    StatisticsGraph(
                        agentCommissions = agentCommissions
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "All",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.secondary
                        ),
                    )
                    IconButton(
                        onClick = {
                            navController.navigate(Route.Transactions.name)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "All Transactions",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(8.dp)
                                .width(40.dp)
                        )
                    }
                }
                TransactionList(transactions = transactions,
                    onTransactionClick = {
                        navController.navigate("transactions/${it.id}")
                    },
                    onRetry = { transaction -> viewModel.retryTransaction(transaction) }
                )
            }
        }

        FloatingActionButton(
            onClick = {
                if (isAppActive) {
                    showStopAppWarningDialog = true
                } else {
                    viewModel.toggleAppState()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 32.dp, end = 32.dp)
        ) {
            Icon(
                imageVector = if (isAppActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = if (isAppActive) "Stop App" else "Start App"
            )
        }
    }

    if (showStopAppWarningDialog) {
        AlertDialog(
            onDismissRequest = { showStopAppWarningDialog = false },
            title = { Text(text = "Stop App") },
            text = { Text(text = "This action will stop the processing of ussd requests for this app until you start again") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.toggleAppState()
                    showStopAppWarningDialog = false
                }) {
                    Text(text = "Stop")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStopAppWarningDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}
