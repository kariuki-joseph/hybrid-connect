package com.example.hybridconnect.presentation.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.domain.enums.OfferType
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.utils.SnackbarManager
import com.example.hybridconnect.presentation.ui.components.CustomButton
import com.example.hybridconnect.presentation.viewmodel.QuickDialViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickDialScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: QuickDialViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()

    val customerPhone by viewModel.customerPhone.collectAsState()
    val offers by viewModel.offers.collectAsState()
    val selectedOffer by viewModel.selectedOffer.collectAsState()
    val isDialing by viewModel.isDialing.collectAsState()
    val ussdResponse by viewModel.ussdResponse.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    val isUssdDialSuccess by viewModel.ussdDialSuccess.collectAsState()

    LaunchedEffect(ussdResponse) {
        ussdResponse?.let {
            dialogMessage = it
            showDialog = true
            viewModel.resetUssdResponse()
        }
    }
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            SnackbarManager.showMessage(scope, it)
            viewModel.resetSnackbarMessage()
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Quick Dial") }, navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        })
    }) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxSize()
        ) {
            QuickDialScreenContent(
                offers = offers,
                customerPhone = customerPhone,
                onCustomerPhoneChanged = { phone -> viewModel.onCustomerPhoneChanged(phone) },
                onOfferChanged = { offer ->
                    val newOffer = if (offer == selectedOffer) null else offer
                    viewModel.onSelectOffer(newOffer)
                },
                onDial = { viewModel.dial() },
                selectedOffer = selectedOffer,
                isDialing = isDialing
            )
        }
    }

    if (showDialog) {
        UssdResponseDialog(
            message = dialogMessage,
            isUssdDialSuccess = isUssdDialSuccess,
            onDismiss = { showDialog = false },
            onFinishClick = { showDialog = false }
        )
    }
}


@Composable
private fun UssdResponseDialog(
    message: String,
    isUssdDialSuccess: Boolean,
    onDismiss: () -> Unit,
    onFinishClick: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .wrapContentWidth()
                .wrapContentHeight()
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                tonalElevation = AlertDialogDefaults.TonalElevation,
                modifier = Modifier.padding(top = 32.dp) // Space for the check icon
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .align(Alignment.CenterHorizontally)
                            .clip(CircleShape)
                            .background(if(isUssdDialSuccess) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isUssdDialSuccess) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = "Success",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(32.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall.copy(
                            textAlign = TextAlign.Start
                        ),
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    CustomButton(
                        onClick = onFinishClick,
                        primary = false,
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        Text(
                            text = "Okay",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun QuickDialScreenContent(
    offers: List<Offer>,
    selectedOffer: Offer?,
    isDialing: Boolean = false,
    customerPhone: String,
    onCustomerPhoneChanged: (phone: String) -> Unit,
    onOfferChanged: (Offer) -> Unit,
    onDial: () -> Unit,
) {
    Log.d("QuickDialScreenContent", "QuickDialScreenContent: ${offers.size}")
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        OutlinedTextField(
            value = customerPhone,
            onValueChange = { onCustomerPhoneChanged(it) },
            label = { Text("Customer Phone") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth(),
            trailingIcon = {
                IconButton(
                    onClick = { onDial() },
                    modifier = Modifier
                        .padding(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    if (isDialing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        IconButton(onClick = onDial) {
                            Icon(
                                imageVector = Icons.Filled.Phone,
                                contentDescription = "Dial",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(1.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(offers) { offer ->
                SelectionChip(
                    offer = offer,
                    isSelected = offer == selectedOffer,
                    onClick = { onOfferChanged(offer) }
                )
            }
        }
    }
}

@Composable
fun SelectionChip(
    offer: Offer,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    FilterChip(
        onClick = onClick,
        label = {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 10.sp
                        )
                    ) {
                        append(offer.name)
                    }
                },
                style = MaterialTheme.typography.bodyMedium
            )
        },
        selected = isSelected,
        trailingIcon = if (isSelected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier
                        .size(12.dp)
                        .padding(0.dp)
                )
            }
        } else {
            null
        },
    )
}

@Preview(showBackground = true)
@Composable
fun QuickDialScreenPreview() {
    val offers = listOf(
        Offer(
            id = UUID.randomUUID(),
            name = "Offer 1",
            ussdCode = "*123#",
            price = 100,
            type = OfferType.DATA
        ),
        Offer(
            id = UUID.randomUUID(),
            name = "Offer 2",
            ussdCode = "*124#",
            price = 200,
            type = OfferType.DATA
        ),
        Offer(
            id = UUID.randomUUID(),
            name = "Offer 3",
            ussdCode = "*125#",
            price = 50,
            type = OfferType.SMS
        ),
        Offer(
            id = UUID.randomUUID(),
            name = "Offer 4",
            ussdCode = "*126#",
            price = 30,
            type = OfferType.DATA
        ),
        Offer(
            id = UUID.randomUUID(),
            name = "Offer 5",
            ussdCode = "*127#",
            price = 500,
            type = OfferType.DATA
        ),
    )


    QuickDialScreenContent(
        offers = offers,
        customerPhone = "0712345678",
        onCustomerPhoneChanged = {},
        onDial = {},
        onOfferChanged = {},
        selectedOffer = offers[1],
        isDialing = false
    )
}