package com.example.hybridconnect.presentation.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.domain.utils.SnackbarManager
import com.example.hybridconnect.presentation.ui.components.CustomButton
import com.example.hybridconnect.presentation.viewmodel.HybridConnectViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HybridConnectScreen(
    modifier: Modifier = Modifier
        .padding(horizontal = 16.dp),
    navController: NavHostController,
    viewModel: HybridConnectViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val isLoading by viewModel.isLoading.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val connectId by viewModel.connectId.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val connectedCount by viewModel.connectedCount.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val connectionStatusProgress by viewModel.connectionStatusProgress.collectAsState()


    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            SnackbarManager.showMessage(scope, it)
            viewModel.resetErrorMessage()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            SnackbarManager.showMessage(scope, it)
            viewModel.resetSuccessMessage()
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Hybrid Connect") },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            HybridConnectScreenContent(
                connectId = connectId,
                isLoading = isLoading,
                isGenerating = isGenerating,
                isOnline = isOnline,
                connectedCount = connectedCount,
                connectionStatusProgress = connectionStatusProgress,
                onGenerateConnectId = { viewModel.generateConnectId() },
                onOnlineStatusToggle = { prevStatus -> viewModel.toggleConnectionStatus(prevStatus) }
            )
        }
    }
}

@Composable
fun HybridConnectScreenContent(
    modifier: Modifier = Modifier.fillMaxSize(),
    connectId: String? = null,
    isLoading: Boolean = false,
    isGenerating: Boolean = false,
    connectionStatusProgress: Boolean = false,
    onGenerateConnectId: () -> Unit,
    onOnlineStatusToggle: (prevStatus: Boolean) -> Unit,
    isOnline: Boolean = false,
    connectedCount: Int = 0,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }

    Box(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 4.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Dot(
                color = if (isOnline) Color.Green else MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$connectedCount connected",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.width(4.dp))

            if (connectionStatusProgress) {
                CircularProgressIndicator(
                    modifier = Modifier.scale(.6f)
                )
            } else {
                Switch(
                    checked = isOnline,
                    onCheckedChange = { onOnlineStatusToggle(isOnline) },
                    modifier = Modifier
                        .scale(.8f),
                )
            }
        }
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            if (connectId != null) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 50.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Your Connect ID",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.ExtraLight,
                        ),
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 50.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Text(
                            text = connectId,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(connectId))
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
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(
                            onClick = {
                                openCustomShareSheet(context, connectId)
                            }
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                    }
                }
            }

            if (connectId == null) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Connect multiple devices with Hybrid Connect. For best performance, make sure this device is connectd to a WiFI network",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                CustomButton(
                    onClick = onGenerateConnectId,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator()
                    } else {
                        Text(
                            "Generate Conect ID",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

private fun openCustomShareSheet(context: Context, connectId: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, connectId)
    }

    val clipboardIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, connectId)
        putExtra(Intent.EXTRA_TITLE, "Copy to Clipboard")
    }

    val chooserIntent = Intent.createChooser(shareIntent, "Share via")
    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(clipboardIntent))

    context.startActivity(chooserIntent)
}

@Composable
private fun Dot(
    color: Color,
) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .background(color, CircleShape)
    )
}

@Preview(showBackground = true)
@Composable
private fun HybridConnectScreenPreview() {
    HybridConnectScreenContent(
        connectId = "BHC-ZX5Y2",
        isLoading = false,
        connectionStatusProgress = true,
        onGenerateConnectId = {},
        onOnlineStatusToggle = {}
    )
}