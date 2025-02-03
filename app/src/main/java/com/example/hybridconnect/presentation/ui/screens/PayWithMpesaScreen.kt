package com.example.hybridconnect.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.R
import com.example.hybridconnect.domain.utils.SnackbarManager
import com.example.hybridconnect.presentation.navigation.Route
import com.example.hybridconnect.presentation.ui.components.CustomButton
import com.example.hybridconnect.presentation.ui.components.PaymentSuccessDialog
import com.example.hybridconnect.presentation.ui.components.ValidatedTextField
import com.example.hybridconnect.presentation.viewmodel.MakePaymentViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayWithMpesaScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
    viewModel: MakePaymentViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val mpesaNumber by viewModel.mpesaNumber.collectAsState()
    val showConfirmationDialog by viewModel.showConfirmationDialog.collectAsState()
    val showStkSuccessDialog by viewModel.showStkSuccessDialog.collectAsState()
    val payViaStkPush by viewModel.payViaStkPush.collectAsState()
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }
    val tillNumber = "123456"
    val isLoading by viewModel.isLoading.collectAsState()
    val mpesaTextFieldError by viewModel.mpesaTextFieldError.collectAsState()
    val mpesaTextFieldErrorMessage by viewModel.mpesaTextFieldErrorMessage.collectAsState()
    val isAwaitingPayment by viewModel.isAwaitingPayment.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val subscription by viewModel.subscriptionPackage.collectAsState()

    if (isCopied) {
        LaunchedEffect(Unit) {
            delay(1500)
            isCopied = false
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            SnackbarManager.showMessage(scope, it)
            viewModel.resetSnackbarMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pay with M-Pesa") },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Pay with M-Pesa",
                style = MaterialTheme.typography.titleMedium,
                color = colorResource(id = R.color.secondary)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { viewModel.onPayViaStkPushChanged(true) }
                    ) {
                        RadioButton(
                            selected = payViaStkPush,
                            onClick = { viewModel.onPayViaStkPushChanged(true) }
                        )
                        Text("STK Push")
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { viewModel.onPayViaStkPushChanged(false) }
                    ) {
                        RadioButton(
                            selected = !payViaStkPush,
                            onClick = { viewModel.onPayViaStkPushChanged(false) }
                        )
                        Text("Follow Instructions")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (payViaStkPush) {
                OutlinedTextField(
                    value = subscription?.price.toString(),
                    readOnly = true,
                    onValueChange = { },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                ValidatedTextField(
                    value = mpesaNumber,
                    onValueChange = { viewModel.onMpesaNumberChanged(it) },
                    label = "M-Pesa Number",
                    isError = mpesaTextFieldError,
                    errorMessage = mpesaTextFieldErrorMessage,
                    keyboardType = KeyboardType.Phone,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                CustomButton(
                    onClick = {
                        if (viewModel.verifyInputs()) {
                            viewModel.onShowConfirmationDialogChanged(true)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Text("Pay KES ${subscription?.price ?: 0}")
                }
            }
            if (!payViaStkPush) {
                Column {
                    Text("Follow the instructions below to complete payment:")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("1. Go to M-Pesa")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("2. Select Lipa na M-Pesa")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("3. Select Buy Goods and Services")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = buildAnnotatedString {
                            append("4. Enter Till Number ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(tillNumber)
                            }
                        })
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(tillNumber))
                            isCopied = true
                        }) {
                            Icon(
                                imageVector = if (isCopied) Icons.Filled.Check else Icons.Filled.ContentCopy,
                                contentDescription = "Copy Till Number"
                            )
                        }
                    }
                    Text(text = buildAnnotatedString {
                        append("5. Enter amount ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("KES ${subscription?.price ?: 0}")
                        }
                    })
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("6. Enter your M-Pesa PIN")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("7. Confirm the transaction")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("8. You will receive a confirmation SMS")
                }
            }
        }
    }

    if (showConfirmationDialog) {
        BasicAlertDialog(
            onDismissRequest = { viewModel.onShowConfirmationDialogChanged(false) },
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.medium,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(R.drawable.send_money),
                            contentDescription = "Send Money",
                            modifier = Modifier.size(180.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = buildAnnotatedString {
                            append("A prompt will be sent to ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(mpesaNumber)
                            }
                            append(" to confirm payment of ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("KES ${subscription?.price ?: 0}. ")
                            }
                            append("Enter your M-Pesa PIN to accept")
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                viewModel.onShowConfirmationDialogChanged(false)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        CustomButton(
                            onClick = {
                                viewModel.initiatePayment()
                            },
                            enabled = !isAwaitingPayment || !isLoading,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = if (isLoading) "Paying..." else "Pay")
                        }
                    }
                }
            }
        }
    }

    if (showStkSuccessDialog) {
        PaymentSuccessDialog(
            message = "Your subscription will be activated on successful payment",
            onDismiss = {},
            onFinishClick = {
            viewModel.onShowStkStatusDialogChanged(false)
            navController?.popBackStack(Route.Home.name, false)
        })
    }
}

@Preview(showBackground = true)
@Composable
private fun MakePaymentScreenPreview() {
    PayWithMpesaScreen()
}
