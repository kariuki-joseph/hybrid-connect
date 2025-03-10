package com.example.hybridconnect.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.domain.enums.OfferType
import com.example.hybridconnect.domain.model.ConnectedApp
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.presentation.navigation.Route
import com.example.hybridconnect.presentation.ui.components.CustomButton
import com.example.hybridconnect.presentation.viewmodel.AppDetailsViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetailsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: AppDetailsViewModel = hiltViewModel(),
    connectId: String,
) {
    val connectedApp by viewModel.connectedApp.collectAsState()
    val availableOffers by viewModel.availableOffers.collectAsState()
    val selectedOffers by viewModel.selectedOffers.collectAsState()
    val connectedOffers by viewModel.connectedOffers.collectAsState()

    var showConfirmDeleteAppDialog by remember { mutableStateOf(false) }


    LaunchedEffect(connectId) {
        viewModel.loadConnectedApp(connectId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Route.AppDetails.title) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            connectedApp?.let { app ->
                AppDetailsScreenContent(
                    connectedApp = app,
                    queueSize = app.messagesSent,
                    connectedOffersCount = connectedOffers.size,
                    availableOffers = availableOffers,
                    selectedOffers = selectedOffers.toList(),
                    onOfferSelectionChange = { offerId, _ ->
                        viewModel.toggleOfferSelection(
                            offerId
                        )
                    },
                    onSaveClick = { viewModel.saveSelectedOffers() },
                    onDeleteClick = {
                        showConfirmDeleteAppDialog = true
                    }
                )
            }
        }
    }

    if (showConfirmDeleteAppDialog) {
        AlertDialog(
            onDismissRequest = {
                showConfirmDeleteAppDialog = false
            },
            title = { Text(text = "Delete App") },
            text = { Text(text = "You won't be able to send messages to this app until you re-connect again") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteConnectedApp();
                    navController.popBackStack()
                    showConfirmDeleteAppDialog = false
                }) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showConfirmDeleteAppDialog = false
                }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

@Composable
private fun AppDetailsScreenContent(
    connectedApp: ConnectedApp,
    queueSize: Int,
    connectedOffersCount: Int = 0,
    availableOffers: List<Offer>,
    selectedOffers: List<UUID>,
    onOfferSelectionChange: (UUID, Boolean) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit = {},
) {
    val sectionTitles = mapOf(
        OfferType.DATA to "Data Offers",
        OfferType.SMS to "SMS Offers",
        OfferType.VOICE to "Minute Offers"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildAnnotatedString {
                    append(connectedApp.connectId)
                    append(" ")
                    withStyle(
                        style = SpanStyle(
                            fontSize = 11.sp
                        )
                    ) {
                        append("(${connectedApp.appName})")
                    }
                },
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(8.dp))
            Dot(isOnline = connectedApp.isOnline)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(queueSize.toString())
                    }
                    append(" Pending, ")
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                        )
                    ) {
                        append(connectedApp.messagesSent.toString())
                    }
                    append(" Sent")
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.secondary
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = connectedOffersCount.toString(),
                style = MaterialTheme.typography.labelSmall
            )

            Text(
                text = " Offers Added",
                style = MaterialTheme.typography.labelSmall
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Available Offers",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                sectionTitles.forEach { (offerType, sectionName) ->
                    val offers = availableOffers.filter { it.type == offerType }
                    if (offers.isNotEmpty()) {
                        item {
                            Text(
                                text = sectionName, // Custom section title
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(offers) { offer ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onOfferSelectionChange(
                                            offer.id,
                                            !selectedOffers.contains(offer.id)
                                        )
                                    }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = selectedOffers.contains(offer.id),
                                    onCheckedChange = { isChecked ->
                                        onOfferSelectionChange(
                                            offer.id,
                                            isChecked
                                        )
                                    }
                                )
                                Text(
                                    text = offer.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        CustomButton(
            onClick = onSaveClick,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "Save")
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Delete App?",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier
                    .clickable { onDeleteClick() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}


@Composable
private fun Dot(
    isOnline: Boolean = false,
) {
    val color = if (isOnline) Color.Green else MaterialTheme.colorScheme.error
    Box(
        modifier = Modifier
            .size(10.dp)
            .background(color, CircleShape)
    )
}


@Preview(showBackground = true)
@Composable
private fun AppDetailsScreenPreview() {
    val connectedApp = ConnectedApp(
        connectId = "BHC-6SHYS",
        appName = "Hybrid Main",
        isOnline = true,
        messagesSent = 30,
    )


    val availableOffers = listOf(
        Offer(UUID.randomUUID(), "Offer 1", "*123#", 10, OfferType.DATA),
        Offer(UUID.randomUUID(), "Offer 2", "*456#", 20, OfferType.VOICE)
    )

    val selectedOffers = remember { mutableStateListOf(availableOffers[0].id) }

    AppDetailsScreenContent(
        connectedApp = connectedApp,
        queueSize = 15,
        availableOffers = availableOffers,
        selectedOffers = selectedOffers,
        onOfferSelectionChange = { offerId, isSelected ->
            if (isSelected) selectedOffers.add(offerId) else selectedOffers.remove(offerId)
        },
        onSaveClick = { }
    )
}