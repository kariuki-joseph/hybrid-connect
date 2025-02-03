package com.example.hybridconnect.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.domain.enums.SiteLinkAccountType
import com.example.hybridconnect.domain.utils.SnackbarManager
import com.example.hybridconnect.presentation.ui.components.CustomButton
import com.example.hybridconnect.presentation.viewmodel.SiteLinkViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSiteLinkScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: SiteLinkViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val isLoading by viewModel.isLoading.collectAsState()
    val siteName by viewModel.siteName.collectAsState()
    val accountNumber by viewModel.accountNumber.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val siteLinkGenerationSuccess by viewModel.siteLinkGenerationSuccess.collectAsState()

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            SnackbarManager.showMessage(scope, it)
            delay(2000)
            viewModel.resetSnackbarMessage()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            SnackbarManager.showMessage(scope, it)
            delay(2000)
            viewModel.resetSnackbarMessage()
        }
    }


    LaunchedEffect(siteLinkGenerationSuccess) {
        if (siteLinkGenerationSuccess) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create SiteLink") },
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
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .imePadding()
        ) {
            CreateSiteLinkScreenContent(
                isLoading = isLoading,
                siteName = siteName,
                accountNumber = accountNumber,
                onSiteNameChanged = { name -> viewModel.onSiteNameChanged(name) },
                onAccountNumberChanged = { acc -> viewModel.onAccountNumberChanged(acc) },
                onAccountTypeChanged = { acc -> viewModel.onAccountTypeChanged(acc) },
                onRequestSiteLink = { viewModel.requestSiteLink() },
            )
        }
    }
}

@Composable
fun CreateSiteLinkScreenContent(
    isLoading: Boolean,
    siteName: String,
    accountNumber: String,
    onSiteNameChanged: (String) -> Unit,
    onAccountNumberChanged: (String) -> Unit,
    onAccountTypeChanged: (SiteLinkAccountType) -> Unit,
    onRequestSiteLink: () -> Unit = {},
) {
    var accountType by remember { mutableStateOf(SiteLinkAccountType.TILL) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        OutlinedTextField(
            value = siteName,
            onValueChange = { onSiteNameChanged(it) },
            label = { Text("Site Name e.g. HybridConnect Enterprises") },
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Words
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Account Type")
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            RadioButton(selected = accountType == SiteLinkAccountType.TILL, onClick = {
                accountType = SiteLinkAccountType.TILL
                onAccountTypeChanged(SiteLinkAccountType.TILL)
            })
            Text("Till", modifier = Modifier.clickable {
                accountType = SiteLinkAccountType.TILL
                onAccountTypeChanged(SiteLinkAccountType.TILL)
            })
            Spacer(modifier = Modifier.weight(2f))

            RadioButton(selected = accountType == SiteLinkAccountType.MPESA, onClick = {
                accountType = SiteLinkAccountType.MPESA
                onAccountTypeChanged(SiteLinkAccountType.MPESA)
            })

            Text("M-Pesa", modifier = Modifier.clickable {
                accountType = SiteLinkAccountType.MPESA
                onAccountTypeChanged(SiteLinkAccountType.MPESA)
            })
            Spacer(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = accountNumber,
            onValueChange = { onAccountNumberChanged(it) },
            label = { Text(text = if (accountType == SiteLinkAccountType.TILL) "Till Number" else "M-Pesa Number") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomButton(
            onClick = onRequestSiteLink,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (!isLoading) "Generate" else "Generating...",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateSiteLinkScreenPreview() {
    CreateSiteLinkScreenContent(
        isLoading = false,
        siteName = "",
        accountNumber = "12654",
        onSiteNameChanged = {},
        onAccountNumberChanged = {},
        onAccountTypeChanged = {},
        onRequestSiteLink = {},
    )
}