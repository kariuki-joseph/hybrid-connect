package com.example.hybridconnect.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.domain.utils.SnackbarManager
import com.example.hybridconnect.presentation.ui.components.CustomButton
import com.example.hybridconnect.presentation.viewmodel.OffersViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOfferScreen(
    viewModel: OffersViewModel = hiltViewModel(),
    offerId: UUID,
    navController: NavHostController? = null,
) {
    val scope = rememberCoroutineScope()
    val offerDetails by viewModel.offerDetails.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val editOfferSuccess by viewModel.editOfferSuccess.collectAsState()
    val isDeleting by viewModel.isDeleting.collectAsState()
    val deleteSuccess by viewModel.deleteSuccess.collectAsState()

    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(offerId) {
        viewModel.getOfferById(offerId)
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            SnackbarManager.showMessage(scope, it)
            viewModel.resetSnackbarMessage()
        }
    }

    LaunchedEffect(editOfferSuccess) {
        if (editOfferSuccess) {
            withContext(Dispatchers.Main) {
                navController?.popBackStack()
            }
        }
    }

    LaunchedEffect(deleteSuccess) {
        if (deleteSuccess) {
            showDeleteConfirmationDialog = false
            viewModel.resetDeleteSuccess()
            navController?.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Offer") },
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
                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = offerDetails.offerName,
                onValueChange = { viewModel.updateOfferDetails(offerDetails.copy(offerName = it)) },
                label = { Text("Offer Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = offerDetails.ussdCode,
                onValueChange = { viewModel.updateOfferDetails(offerDetails.copy(ussdCode = it)) },
                label = { Text("USSD Code e.g. *188*6*1*BH*1#") },
                keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Characters),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = offerDetails.price,
                onValueChange = { viewModel.updateOfferDetails(offerDetails.copy(price = it)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                label = { Text("Price e.g. 20") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    showDeleteConfirmationDialog = true
                }) {
                    Text(
                        text = "Delete Offer?",
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
                onClick = { viewModel.updateOffer() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Update",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            DeleteOfferConfirmationDialog(
                showDialog = showDeleteConfirmationDialog,
                isDeleting = isDeleting,
                onDismiss = {
                    showDeleteConfirmationDialog = false
                },
                onConfirm = {
                    viewModel.deleteOffer()
                }
            )
        }
    }
}

@Composable
fun DeleteOfferConfirmationDialog(
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
                Text(text = "Are you sure you want to delete this offer?")
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