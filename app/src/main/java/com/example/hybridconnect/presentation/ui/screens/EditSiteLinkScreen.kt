package com.example.hybridconnect.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
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
fun EditSiteLinkScreen(
    modifier: Modifier = Modifier
        .imePadding()
        .padding(horizontal = 16.dp, vertical = 8.dp),
    navController: NavHostController,
    viewModel: SiteLinkViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val isLoading by viewModel.isLoading.collectAsState()
    val siteName by viewModel.siteName.collectAsState()
    val accountNumber by viewModel.accountNumber.collectAsState()
    val accountType by viewModel.accountType.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val siteLinkGenerationSuccess by viewModel.siteLinkGenerationSuccess.collectAsState()
    val isDeleting by viewModel.isDeleting.collectAsState()
    val deleteSuccess by viewModel.deleteSuccess.collectAsState()

    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getAndFillSiteLinkDetails()
    }

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

    LaunchedEffect(deleteSuccess) {
        if (deleteSuccess) {
            showDeleteConfirmationDialog = false
            viewModel.resetDeleteSuccess()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit SiteLink") },
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
        ) {
            EditSiteLinkScreenContent(
                isLoading = isLoading,
                siteName = siteName,
                accountNumber = accountNumber,
                accountType = accountType,
                onSiteNameChanged = { name -> viewModel.onSiteNameChanged(name) },
                onAccountNumberChanged = { acc -> viewModel.onAccountNumberChanged(acc) },
                onAccountTypeChanged = { acc -> viewModel.onAccountTypeChanged(acc) },
                onUpdateSiteLink = { viewModel.updateSiteLink() },
                onDeleteSiteLinkClick = { showDeleteConfirmationDialog = true }
            )

            DeleteSiteLinkConfirmationDialog(
                showDialog = showDeleteConfirmationDialog,
                isDeleting = isDeleting,
                onDismiss = {
                    showDeleteConfirmationDialog = false
                },
                onConfirm = {
                    viewModel.deleteSiteLink()
                }
            )
        }
    }
}

@Composable
fun EditSiteLinkScreenContent(
    isLoading: Boolean,
    siteName: String,
    accountNumber: String,
    accountType: SiteLinkAccountType = SiteLinkAccountType.TILL,
    onSiteNameChanged: (String) -> Unit,
    onAccountNumberChanged: (String) -> Unit,
    onAccountTypeChanged: (SiteLinkAccountType) -> Unit,
    onUpdateSiteLink: () -> Unit = {},
    onDeleteSiteLinkClick: () -> Unit = {},
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(scrollState)
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
                onAccountTypeChanged(SiteLinkAccountType.TILL)
            })
            Text("Till", modifier = Modifier.clickable {
                onAccountTypeChanged(SiteLinkAccountType.TILL)
            })
            Spacer(modifier = Modifier.weight(2f))

            RadioButton(selected = accountType == SiteLinkAccountType.MPESA, onClick = {
                onAccountTypeChanged(SiteLinkAccountType.MPESA)
            })

            Text("M-Pesa", modifier = Modifier.clickable {
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
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onDeleteSiteLinkClick) {
                Text(
                    text = "Delete SiteLink?",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.ExtraLight
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        CustomButton(
            onClick = onUpdateSiteLink,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (!isLoading) "Update" else "Updating...",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun DeleteSiteLinkConfirmationDialog(
    showDialog: Boolean,
    isDeleting: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Confirm Delete")
            },
            text = {
                Text(text = "Your site will no longer be accessible by your customers. Proceed?")
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(text = "Delete")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EditSiteLinkScreenPreview() {
    EditSiteLinkScreenContent(
        isLoading = false,
        siteName = "",
        accountNumber = "12654",
        onSiteNameChanged = {},
        onAccountNumberChanged = {},
        onAccountTypeChanged = {},
        onUpdateSiteLink = {},
        onDeleteSiteLinkClick = {}
    )
}