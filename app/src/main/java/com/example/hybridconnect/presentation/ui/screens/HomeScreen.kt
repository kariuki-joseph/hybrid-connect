package com.example.hybridconnect.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.domain.utils.SnackbarManager
import com.example.hybridconnect.presentation.navigation.Route
import com.example.hybridconnect.presentation.ui.components.ConnectedAppComponent
import com.example.hybridconnect.presentation.ui.components.CustomButton
import com.example.hybridconnect.presentation.viewmodel.HomeViewModel
import com.example.hybridconnect.presentation.viewmodel.SmsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    smsViewModel: SmsViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val agentFirstName by viewModel.agentFirstName.collectAsState()
    val greetings by viewModel.greetings.collectAsState()
    val isAppActive by viewModel.isAppActive.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val connectedApps by viewModel.connectedApps.collectAsState()
    val queueSize by viewModel.queueSize.collectAsState()
    val connectedOffersCount by viewModel.connectedOffersCount.collectAsState()
    var showStopAppWarningDialog by remember { mutableStateOf(false) }
    val logoutSuccess by viewModel.logoutSuccess.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val context = LocalContext.current

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

    LaunchedEffect(true) {
        smsViewModel.readMPEMASMS(context = context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        DrawerScaffoldScreen(
            navController = navController,
            topBarTitle = "$greetings, $agentFirstName",
            onLogout = { viewModel.logoutUser() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                ) {
                    Text(
                        text = "Connected Apps",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterEnd),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = { viewModel.toggleOnlineState() }
                        ) {
                            Icon(
                                imageVector = if (isConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                                contentDescription = if (isAppActive) "Wifi Off" else "Wifi On",
                                tint = if (isConnected) Color.Green else MaterialTheme.colorScheme.error
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                navController.navigate(Route.AddConnectedApp.name)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddBox,
                                contentDescription = "Add App",
                                tint = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .weight(1f)
                ) {
                    items(connectedApps) { app ->
                        val offersCount = connectedOffersCount[app.connectId] ?: 0
                        ConnectedAppComponent(
                            connectedApp = app,
                            queueSize = queueSize,
                            connectedOffersCount = offersCount,
                            onClick = {
                                navController.navigate(
                                    Route.AppDetails.name
                                        .replace("{connectId}", app.connectId)
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }

//        TestSendComponent(
//            modifier = Modifier
//                .fillMaxWidth()
//                .align(Alignment.BottomStart)
//                .padding(start = 16.dp, bottom = 32.dp),
//            onTestButtonClicked = { viewModel.testButtonClicked(it) }
//        )

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


@Composable
fun TestSendComponent(
    modifier: Modifier = Modifier,
    onTestButtonClicked: (String) -> Unit,
) {
    Row(
        modifier = modifier
    ) {
        var amount by remember { mutableStateOf("5") }
        TextField(
            value = amount,
            onValueChange = { value ->
                amount = value
            },
            modifier = Modifier.width(70.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        CustomButton(onClick = { onTestButtonClicked(amount) }) {
            Text("Test Send")
        }
    }
}
