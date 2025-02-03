package com.example.hybridconnect.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.domain.utils.SnackbarManager
import com.example.hybridconnect.presentation.navigation.Route
import com.example.hybridconnect.presentation.ui.components.Keypad
import com.example.hybridconnect.presentation.ui.components.PinRow
import com.example.hybridconnect.presentation.viewmodel.PinSetupViewModel

@Composable
fun PinSetupScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
    viewModel: PinSetupViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val pin by viewModel.pin.collectAsState()
    val confirmPin by viewModel.confirmPin.collectAsState()
    val isConfirming by viewModel.isConfirming.collectAsState()
    val isPinError by viewModel.isPinError.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val pinSetupSuccess by viewModel.pinSetupSuccess.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    LaunchedEffect(pinSetupSuccess) {
        if (pinSetupSuccess) {
            navController?.navigate(Route.Home.name)
        }
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            SnackbarManager.showMessage(scope, it)
            viewModel.resetSnackbarMessage()
        }
    }

    PinSetupScreenContent(
        modifier = modifier,
        pin = pin,
        confirmPin = confirmPin,
        isLoading = isLoading,
        isConfirming = isConfirming,
        isPinError = isPinError,
        onNumberClick = { number -> viewModel.onNumberClick(number) },
        onDeletePin = { viewModel.onDeletePin() }
    )

}

@Composable
fun PinSetupScreenContent(
    modifier: Modifier = Modifier,
    pin: String = "",
    confirmPin: String = "",
    isLoading: Boolean = false,
    isConfirming: Boolean = false,
    isPinError: Boolean = false,
    onNumberClick: (number: String) -> Unit,
    onDeletePin: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = when {
                            isPinError -> "PIN Mismatch!"
                            isConfirming -> "Confirm your PIN"
                            else -> "Setup your PIN"
                        },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        "Enter your desired 4-digit PIN",
                        style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    PinRow(
                        pin = if (isConfirming) confirmPin else pin,
                        isLoading = isLoading,
                        hasError = isPinError,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Keypad(
                        onNumberClick = { number -> onNumberClick(number) },
                        onDeleteClick = { onDeletePin() },
                        onFingerprintClick = {},
                        pin = if (isConfirming) confirmPin else pin,
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSetupPinScreen() {
    PinSetupScreenContent(
        pin = "123",
        isLoading = false,
        isConfirming = false,
        isPinError = false,
        onNumberClick = {},
        onDeletePin = {}
    )
}