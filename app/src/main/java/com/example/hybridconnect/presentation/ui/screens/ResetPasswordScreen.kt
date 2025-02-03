package com.example.hybridconnect.presentation.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.domain.utils.SnackbarManager
import com.example.hybridconnect.presentation.navigation.Route
import com.example.hybridconnect.presentation.ui.components.CustomButton
import com.example.hybridconnect.presentation.ui.components.ValidatedTextField
import com.example.hybridconnect.presentation.viewmodel.ForgotPasswordViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
    passedEmail: String = "",
) {
    val scope = rememberCoroutineScope()
    val email by viewModel.email.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val sendSuccess by viewModel.sendSuccess.collectAsState()

    LaunchedEffect(passedEmail) {
        if (email.isNotEmpty()) {
            viewModel.onEmailChanged(passedEmail)
        }
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            SnackbarManager.showMessage(scope, it)
            viewModel.resetSnackbarMessage()
        }
    }

    LaunchedEffect(sendSuccess) {
        if (sendSuccess) {
            navController.navigate(Route.OtpVerification.name.replace("{email}", email))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Reset PIN",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            withContext(Dispatchers.Main) {
                                navController.popBackStack()
                            }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            ResetPasswordScreenContent(
                email = email,
                emailError = emailError,
                isLoading = isLoading,
                onEmailChanged = { viewModel.onEmailChanged(it) },
                onSubmit = { viewModel.sendResetPin() }
            )
        }
    }
}

@Composable
fun ResetPasswordScreenContent(
    email: String = "",
    isLoading: Boolean = false,
    onEmailChanged: (String) -> Unit,
    emailError: String? = null,
    onSubmit: () -> Unit,
) {
    Column {
        Spacer(modifier = Modifier.height(16.dp))
        ValidatedTextField(
            value = email,
            onValueChange = { onEmailChanged(it) },
            label = "Enter your Email",
            isError = emailError != null,
            errorMessage = emailError,
            keyboardType = KeyboardType.Email,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            CustomButton(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(
                        text = "Send",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier
                            .padding(horizontal = 32.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ResetPasswordScreenPreview() {
    ResetPasswordScreenContent(
        email = "john.doe@gmail.com",
        onEmailChanged = {},
        emailError = null,
        onSubmit = {}
    )
}