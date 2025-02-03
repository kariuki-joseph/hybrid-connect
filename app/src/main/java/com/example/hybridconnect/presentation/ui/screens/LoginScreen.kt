package com.example.hybridconnect.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.presentation.navigation.Route
import com.example.hybridconnect.presentation.ui.components.CustomButton
import com.example.hybridconnect.presentation.ui.components.ValidatedTextField
import com.example.hybridconnect.presentation.viewmodel.PinLoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
    viewModel: PinLoginViewModel = hiltViewModel(),
) {

    val email by viewModel.email.collectAsState()
    val emailError by viewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Login",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            LoginScreenContent(
                email = email,
                onEmailChanged = { email -> viewModel.onEmailChanged(email) },
                emailError = emailError,
                onSubmit = {
                    if (viewModel.validateEmail()) {
                        navController?.navigate("pin-login/${email}")
                    }
                },
                onRegisterClick = {
                    navController?.navigate(Route.Register.name)
                },
                onForgotPin = {
                    navController?.navigate(Route.ResetPassword.name.replace("{email}", email))
                }
            )
        }
    }
}

@Composable
fun LoginScreenContent(
    email: String = "",
    onEmailChanged: (String) -> Unit,
    emailError: String? = null,
    onSubmit: () -> Unit,
    onRegisterClick: () -> Unit = {},
    onForgotPin: () -> Unit = {}
) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Welcome Back!",
            style = MaterialTheme.typography.titleMedium,
        )
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
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onForgotPin) {
                Text(
                    text = "Forgot PIN?",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraLight
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            CustomButton(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Continue",
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

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreenContent(
        email = "",
        onEmailChanged = {},
        onSubmit = { },
        onForgotPin = {}
    )
}