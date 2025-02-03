package com.example.hybridconnect.presentation.ui.screens

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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.R
import com.example.hybridconnect.domain.utils.SnackbarManager
import com.example.hybridconnect.presentation.navigation.Route
import com.example.hybridconnect.presentation.ui.components.CustomButton
import com.example.hybridconnect.presentation.ui.components.ValidatedTextField
import com.example.hybridconnect.presentation.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val registerSuccess by viewModel.registerSuccess.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()

    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val email by viewModel.email.collectAsState()

    val firstNameError by viewModel.firstNameError.collectAsState()
    val lastNameError by viewModel.lastNameError.collectAsState()
    val phoneNumberError by viewModel.phoneNumberError.collectAsState()
    val emailError by viewModel.emailError.collectAsState()

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            SnackbarManager.showMessage(scope, it)
            viewModel.resetSnackbarMessage()
        }
    }

    LaunchedEffect(registerSuccess) {
        if (registerSuccess) {
            navController?.navigate(Route.OtpVerification.name.replace("{email}", email))
        }
    }


    Scaffold(modifier = Modifier.fillMaxSize(), content = { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            RegisterScreenContent(
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNumber,
                email = email,
                isLoading = isLoading,
                firstNameError = firstNameError,
                lastNameError = lastNameError,
                phoneNumberError = phoneNumberError,
                emailError = emailError,
                onFirstNameChanged = { viewModel.onFirstNameChanged(it) },
                onLastNameChanged = { viewModel.onLastNameChanged(it) },
                onPhoneNumberChanged = { viewModel.onPhoneNumberChanged(it) },
                onEmailChanged = { viewModel.onEmailChanged(it) },
                onRegister = { viewModel.register()
                             },
                onRegisterWithGoogle = { viewModel.registerWithGoogle() },
                onGoToLogin = { navController?.navigate(Route.Login.name) }
            )
        }
    })
}

@Composable
fun RegisterScreenContent(
    firstName: String,
    lastName: String,
    phoneNumber: String,
    email: String,
    isLoading: Boolean = false,
    firstNameError: String? = null,
    lastNameError: String? = null,
    phoneNumberError: String? = null,
    emailError: String? = null,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onPhoneNumberChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onRegister: () -> Unit,
    onRegisterWithGoogle: () -> Unit = {},
    onGoToLogin: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Create an Account With Us",
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            ),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ValidatedTextField(
                label = "First Name",
                value = firstName,
                onValueChange = { onFirstNameChanged(it) },
                isError = firstNameError != null,
                errorMessage = firstNameError,
                modifier = Modifier.weight(1f)
            )

            ValidatedTextField(
                label = "Last Name",
                value = lastName,
                onValueChange = { onLastNameChanged(it) },
                isError = lastNameError != null,
                errorMessage = lastNameError,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        ValidatedTextField(
            label = "Phone Number",
            value = phoneNumber,
            onValueChange = { onPhoneNumberChanged(it) },
            isError = phoneNumberError != null,
            errorMessage = phoneNumberError,
            keyboardType = KeyboardType.Phone,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        ValidatedTextField(
            label = "Email",
            value = email,
            onValueChange = { onEmailChanged(it) },
            isError = emailError != null,
            errorMessage = emailError,
            keyboardType = KeyboardType.Email,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        CustomButton(
            onClick = onRegister,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.CenterStart),
                    )
                }
                Text(
                    text = if(isLoading) "Creating Account..." else "Create Account",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account?", style = MaterialTheme.typography.bodyMedium
            )

            TextButton(
                onClick = onGoToLogin
            ) {
                Text(
                    text = AnnotatedString("Login"),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegisterScreen() {
    RegisterScreenContent(
        firstName = "",
        lastName = "",
        phoneNumber = "",
        email = "",
        isLoading = true,
        firstNameError = null,
        lastNameError = null,
        phoneNumberError = null,
        emailError = null,
        onFirstNameChanged = {},
        onLastNameChanged = {},
        onPhoneNumberChanged = {},
        onEmailChanged = {},
        onRegister = {},
        onRegisterWithGoogle = {},
        onGoToLogin = {}
    )
}