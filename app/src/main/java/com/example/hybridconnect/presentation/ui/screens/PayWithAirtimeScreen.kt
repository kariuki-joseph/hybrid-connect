package com.example.hybridconnect.presentation.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.domain.enums.SubscriptionType
import com.example.hybridconnect.domain.model.SubscriptionPackage
import com.example.hybridconnect.domain.utils.SnackbarManager
import com.example.hybridconnect.presentation.navigation.Route
import com.example.hybridconnect.presentation.ui.components.CustomButton
import com.example.hybridconnect.presentation.ui.components.PaymentSuccessDialog
import com.example.hybridconnect.presentation.viewmodel.MakePaymentViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayWithAirtimeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
    viewModel: MakePaymentViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val subscription by viewModel.subscriptionPackage.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val subscriptionSuccess by viewModel.subscriptionSuccess.collectAsState()
    val subscriptionMessage by viewModel.subscriptionMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showSubscriptionSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            SnackbarManager.showMessage(scope, it)
            viewModel.resetSnackbarMessage()
        }
    }

    LaunchedEffect(subscriptionSuccess){
        showSubscriptionSuccessDialog = subscriptionSuccess
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pay With Airtime") },
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
            PayWithAirtimeScreenContent(
                subscriptionPackage = subscription,
                onPay = { viewModel.payWithAirtime() },
                isLoading = isLoading
            )
        }
    }

    if (showSubscriptionSuccessDialog) {
        PaymentSuccessDialog(
            message = subscriptionMessage,
            onDismiss = {},
            onFinishClick = {
                showSubscriptionSuccessDialog = false
            navController?.popBackStack(Route.Home.name, false)
        })
    }
}

@Composable
fun PayWithAirtimeScreenContent(
    subscriptionPackage: SubscriptionPackage?,
    onPay: () -> Unit,
    isLoading: Boolean = false,
) {

    Column {
        OutlinedTextField(
            value = subscriptionPackage?.price.toString(),
            readOnly = true,
            onValueChange = { },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "NOTE: Your default HybridConnect USSD Sim will be used to complete this request")
        Spacer(modifier = Modifier.height(24.dp))
        CustomButton(
            onClick = onPay,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isLoading) "Paying..." else "Pay KES ${subscriptionPackage?.price ?: 0}",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PayWithAirtimeScreenPreview() {
    val subscriptionPackage = SubscriptionPackage(
        id = UUID.randomUUID(),
        name = "Daily Plan",
        price = 1000,
        description = "Enjoy exclusive features and benefits.",
        limit = (10 * 60 * 100).toDouble(),
        type = SubscriptionType.UNLIMITED
    )

    PayWithAirtimeScreenContent(
        subscriptionPackage = subscriptionPackage,
        onPay = {},
        isLoading = true
    )
}
