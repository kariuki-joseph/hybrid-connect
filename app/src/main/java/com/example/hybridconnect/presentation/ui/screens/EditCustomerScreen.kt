package com.example.hybridconnect.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.domain.utils.SnackbarManager
import com.example.hybridconnect.presentation.ui.components.CustomButton
import com.example.hybridconnect.presentation.viewmodel.CustomersViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCustomerScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: CustomersViewModel = hiltViewModel(),
    customerPhone: String,
) {
    val scope = rememberCoroutineScope()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val editSuccess by viewModel.editSuccess.collectAsState()
    val deleteSuccess by viewModel.deleteSuccess.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val customerName by viewModel.customerName.collectAsState()
    val accountBalance by viewModel.accountBalance.collectAsState()

    LaunchedEffect(customerPhone) {
        if (customerPhone.isNotEmpty()) {
            viewModel.getCustomerByPhone(customerPhone)
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            SnackbarManager.showMessage(scope, it)
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            SnackbarManager.showMessage(scope, it)
            viewModel.resetSnackbarMessage()
        }
    }

    LaunchedEffect(editSuccess) {
        if (editSuccess) {
            navController.popBackStack()
            viewModel.resetEditSuccess()
        }
    }

    LaunchedEffect(deleteSuccess) {
        if (deleteSuccess) {
            navController.popBackStack()
            viewModel.resetDeleteSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Customer") },
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
        ) {
            EditCustomerScreenContent(
                customerName = customerName,
                accountBalance = accountBalance,
                customerPhone = customerPhone,
                deleteSuccess = deleteSuccess,
                isLoading = isLoading,
                onCustomerNameChanged = { viewModel.onCustomerNameChanged(it) },
                onAccountBalanceChanged = { viewModel.onAccountBalanceChanged(it) },
                onSave = { viewModel.updateCustomer() },
                onDelete = { viewModel.deleteCustomer() },
            )
        }
    }
}

@Composable
private fun EditCustomerScreenContent(
    customerName: String,
    accountBalance: String,
    customerPhone: String,
    deleteSuccess: Boolean,
    isLoading: Boolean,
    onCustomerNameChanged: (String) -> Unit,
    onAccountBalanceChanged: (String) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
) {
    val scrollState = rememberScrollState()
    var showDeleteCustomerDialog by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(deleteSuccess) {
        if (deleteSuccess) {
            showDeleteCustomerDialog = false
        }
    }

    Column(
        modifier = Modifier
            .imePadding()
            .verticalScroll(scrollState)
    ) {
        OutlinedTextField(
            value = customerName,
            onValueChange = { onCustomerNameChanged(it) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            label = { Text("Customer Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = customerPhone,
            readOnly = true,
            onValueChange = { },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            label = { Text("Customer Phone") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = accountBalance,
            onValueChange = { onAccountBalanceChanged(it) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            label = { Text("Account Balance") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = {
                showDeleteCustomerDialog = true
            }) {
                Text(
                    text = "Delete Customer?",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.ExtraLight
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        CustomButton(
            primary = true,
            onClick = onSave,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = "Update",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }

    DeleteCustomerConfirmationDialog(
        showDialog = showDeleteCustomerDialog,
        onConfirm = onDelete,
        onDismiss = {
            keyboardController?.hide()
            showDeleteCustomerDialog = false
        }
    )
}

@Composable
fun DeleteCustomerConfirmationDialog(
    showDialog: Boolean,
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
                Text(text = "This action will also delete all transactions made by this customer. Proceed?")
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(
                        text = "Delete"
                    )
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
private fun EditCustomerScreenPreview() {
    EditCustomerScreenContent(
        customerName = "John Doe",
        accountBalance = "120",
        customerPhone = "0712345678",
        deleteSuccess = false,
        isLoading = false,
        onCustomerNameChanged = {},
        onAccountBalanceChanged = {},
        onSave = {},
        onDelete = {},
    )
}