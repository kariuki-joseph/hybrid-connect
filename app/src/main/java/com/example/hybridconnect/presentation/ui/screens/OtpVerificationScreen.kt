package com.example.hybridconnect.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.domain.utils.SnackbarManager
import com.example.hybridconnect.presentation.navigation.Route
import com.example.hybridconnect.presentation.ui.components.CustomButton
import com.example.hybridconnect.presentation.viewmodel.OtpVerificationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
    email: String,
    viewModel: OtpVerificationViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val otpCode by viewModel.otp.collectAsState() // OTP code as a list of digits
    val timerSeconds by viewModel.timerSeconds.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val otpVerificationSuccess by viewModel.otpVerificationSuccess.collectAsState()

    LaunchedEffect(otpVerificationSuccess) {
        if (otpVerificationSuccess) {
            navController?.navigate(Route.PinSetup.name)
        }
    }

    LaunchedEffect(email) {
        if (email.isNotEmpty()) {
            viewModel.setEmail(email)
        }
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            SnackbarManager.showMessage(scope, it)
            viewModel.resetSnackbarMessage()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Verify OTP",
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
                                navController?.popBackStack()
                            }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            verticalArrangement = Arrangement.Center
        ) {
            OtpVerificationScreenContent(
                otpCode = otpCode,
                timerSeconds = timerSeconds,
                email = email,
                isLoading = isLoading,
                onOtpSubmit = { viewModel.verifyOtp() },
                onOtpResend = { viewModel.resendOtp() },
                onOtpCodeChange = { index, digit ->
                    viewModel.onOtpChanged(
                        index,
                        digit
                    )
                } // Pass OTP change to ViewModel
            )
        }
    }

}

@Composable
fun OtpVerificationScreenContent(
    modifier: Modifier = Modifier,
    otpCode: List<String>, // OTP code as a list of digits
    timerSeconds: Int,
    email: String,
    isLoading: Boolean,
    onOtpSubmit: () -> Unit,
    onOtpResend: () -> Unit,
    onOtpCodeChange: (Int, String) -> Unit, // Function to handle OTP changes, now takes index and digit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(MaterialTheme.typography.bodyLarge.toSpanStyle()) {
                    append("Enter the 6-digit code sent to ")
                }
                withStyle(
                    MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        .toSpanStyle()
                ) {
                    append(email)
                }
            },
            style = MaterialTheme.typography.bodyLarge.copy(
                textAlign = TextAlign.Center,
            ),
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

//        Text(
//            text = formatTime(timerSeconds),
//            color = MaterialTheme.colorScheme.primary,
//            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
//        )

        Spacer(modifier = Modifier.height(24.dp))

        OTPInputBoxes(otpCode, onOtpCodeChange)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            OtpResendText {
                onOtpResend()
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        CustomButton(
            onClick = onOtpSubmit,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text(
                    "Verify",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )
            }
        }
    }
}

// Format the timer display as MM:SS
@Composable
fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secondsLeft = seconds % 60
    return String.format(Locale.ENGLISH, "%02d:%02d", minutes, secondsLeft)
}

@Composable
fun OTPInputBoxes(otpCode: List<String>, onOtpCodeChange: (Int, String) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequesterList = List(6) { FocusRequester() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(6) { index ->
            OutlinedTextField(
                value = otpCode.getOrElse(index) { "" },
                onValueChange = { value ->
                    if (value.length == 1 && value.first().isDigit()) {
                        // Handle adding a digit to the OTP code
                        val digit = value.first().toString()
                        onOtpCodeChange(index, digit) // Emit the updated digit
                        if (index < 5) {
                            focusRequesterList[index + 1].requestFocus() // Move to next field
                        }
                    } else if (value.isEmpty()) {
                        // Handle backspace: If the field is cleared, move to the previous field
                        if (index > 0) {
                            focusRequesterList[index - 1].requestFocus()
                        }
                        onOtpCodeChange(index, "") // Clear the digit in the OTP list
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide() // Hide the keyboard when done
                    }
                ),
                modifier = Modifier
                    .size(48.dp)
                    .padding(0.dp)
                    .focusRequester(focusRequesterList[index]),
                textStyle = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                singleLine = true,
                maxLines = 1,
                shape = MaterialTheme.shapes.medium,
            )
        }
    }
}

@Composable
fun OtpResendText(onResendClick: () -> Unit) {
    val annotatedText = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.onBackground,
            )
        ) {
            append("Didn't receive the code? ")
        }
        pushStringAnnotation(
            tag = "RESEND",
            annotation = "resend"
        )
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                textDecoration = TextDecoration.Underline // Optional: underline for emphasis
            )
        ) {
            append("Resend")
        }
        pop()
    }

    ClickableText(
        text = annotatedText,
        style = MaterialTheme.typography.bodyLarge,
        onClick = { offset ->
            annotatedText.getStringAnnotations(tag = "RESEND", start = offset, end = offset)
                .firstOrNull()?.let {
                    onResendClick()
                }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun OtpVerificationScreenPreview() {
    OtpVerificationScreenContent(
        otpCode = MutableList(6) { "2" },
        timerSeconds = 120,
        email = "example@mail.com",
        isLoading = false,
        onOtpSubmit = {},
        onOtpResend = {},
        onOtpCodeChange = { _, _ -> },
    )
}