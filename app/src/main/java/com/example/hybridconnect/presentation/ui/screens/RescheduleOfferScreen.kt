package com.example.hybridconnect.presentation.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.domain.enums.OfferType
import com.example.hybridconnect.domain.enums.RenewInterval
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.utils.SnackbarManager
import com.example.hybridconnect.presentation.ui.components.CustomButton
import com.example.hybridconnect.presentation.ui.components.SingleLineTextField
import com.example.hybridconnect.presentation.viewmodel.RescheduleViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RescheduleOfferScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
    viewModel: RescheduleViewModel = hiltViewModel(),
    transactionId: String,
) {
    val scope = rememberCoroutineScope()
    val transaction by viewModel.transaction.collectAsState()
    val offers by viewModel.offers.collectAsState()
    val rescheduleSuccess by viewModel.rescheduleSuccess.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    LaunchedEffect(transactionId) {
        if (transactionId.isNotEmpty()) {
            viewModel.getTransaction(transactionId)
        }
    }

    LaunchedEffect(rescheduleSuccess) {
        if (rescheduleSuccess) {
            delay(1000)
            navController?.popBackStack()
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
                title = { Text("Reschedule Offer") },
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
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            RescheduleOfferScreenContent(
                onReschedule = { offer, time ->
                    viewModel.rescheduleOffer(transaction, offer, time)
                },
                offers = offers,
                onAutoRenew = { renewCount, interval ->
                    viewModel.onAutoRenew(renewCount, interval)
                }
            )
        }
    }
}

@Composable
fun RescheduleOfferScreenContent(
    modifier: Modifier = Modifier,
    onReschedule: (offer: Offer?, time: Long) -> Unit,
    onAutoRenew: (renewCount: Int, interval: RenewInterval) -> Unit,
    offers: List<Offer>,
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val calendar = Calendar.getInstance()

    var offerExpanded by remember { mutableStateOf(false) }
    var repeatCountIntervalExpanded by remember { mutableStateOf(false) }

    var selectedOffer by remember { mutableStateOf<Offer?>(null) }
    var selectedDateTime by remember {
        mutableLongStateOf(Calendar.getInstance().apply { add(Calendar.MINUTE, 1) }.timeInMillis)
    }
    var autoRenew by remember { mutableStateOf(false) }
    var renewCount by remember { mutableStateOf("") }

    val renewIntervals = listOf(
        RenewInterval.MINUTE,
        RenewInterval.HOUR,
        RenewInterval.DAY,
        RenewInterval.WEEK,
        RenewInterval.MONTH
    )

    var renewIntervalUnit by remember { mutableStateOf(RenewInterval.DAY) }

    Column(
        modifier = modifier
            .imePadding()
            .verticalScroll(scrollState),
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable { offerExpanded = true }
            .background(MaterialTheme.colorScheme.surface)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedOffer?.name ?: "Select Offer",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
                Icon(
                    imageVector = if (offerExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = if (offerExpanded) "Collapse Dropdown" else "Expand Dropdown",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            DropdownMenu(
                expanded = offerExpanded,
                onDismissRequest = { offerExpanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                offers.forEach { offer ->
                    DropdownMenuItem(onClick = {
                        selectedOffer = offer
                        offerExpanded = false
                    }, text = {
                        Text(
                            text = offer.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        selectedOffer?.let { offer ->
            Column(
                modifier = modifier.fillMaxWidth()
            ) {
                Text(text = buildAnnotatedString {
                    withStyle(
                        style = MaterialTheme.typography.bodyMedium.toSpanStyle().copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append("Ksh ${offer.price}")
                    }
                    append(" -> ")
                    withStyle(style = MaterialTheme.typography.bodySmall.toSpanStyle()) {
                        append(offer.ussdCode)
                    }
                })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = SimpleDateFormat("EEE dd/MM/yyyy", Locale.getDefault()).format(
                Date(
                    selectedDateTime
                )
            ), modifier = Modifier
                .clickable {
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            calendar.set(year, month, dayOfMonth)
                            selectedDateTime = calendar.timeInMillis
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
                .padding(vertical = 16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = SimpleDateFormat(
                "h:mm a", Locale.getDefault()
            ).format(Date(selectedDateTime)), modifier = Modifier
                .clickable {
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            calendar.apply {
                                set(Calendar.HOUR_OF_DAY, hourOfDay)
                                set(Calendar.MINUTE, minute)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            selectedDateTime = calendar.timeInMillis
                        },
                        calendar
                            .apply {
                                add(Calendar.MINUTE, 1)
                            }
                            .get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        false
                    ).show()
                }
                .padding(16.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Checkbox(checked = autoRenew, onCheckedChange = { autoRenew = it })
            Text(text = "Auto-Renew",
                modifier = Modifier.clickable { autoRenew = !autoRenew })
        }
        if (autoRenew) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Renew daily for the next ")
                Spacer(modifier = Modifier.width(8.dp))
                SingleLineTextField(intervalValue = renewCount,
                    onValueChange = { newValue ->
                        renewCount = newValue
                        onAutoRenew(
                            if (newValue.isNotEmpty()) newValue.toInt() else 0,
                            renewIntervalUnit
                        )
                    })
                Spacer(modifier = Modifier.width(8.dp))
                Box {
                    Text(text = "${
                        renewIntervalUnit.name.lowercase()
                            .replaceFirstChar { it.uppercase() }
                    }(s)",
                        modifier = Modifier
                            .clickable { repeatCountIntervalExpanded = true }
                            .padding(8.dp))
                    DropdownMenu(expanded = repeatCountIntervalExpanded,
                        onDismissRequest = { repeatCountIntervalExpanded = false }) {
                        renewIntervals.forEach { interval ->
                            DropdownMenuItem(onClick = {
                                renewIntervalUnit = interval
                                repeatCountIntervalExpanded = false
                                onAutoRenew(renewCount.toInt(), renewIntervalUnit)
                            }, text = {
                                Text(text = "${
                                    interval.name.lowercase()
                                        .replaceFirstChar { it.uppercase() }
                                }(s)")
                            })
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        CustomButton(modifier = Modifier.fillMaxWidth(), onClick = {
            onReschedule(selectedOffer, selectedDateTime)
        }) {
            Text(
                text = "Reschedule",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun RescheduleOfferScreenPreview() {
    val offers = listOf(
        Offer(
            id = UUID.randomUUID(),
            name = "2GB for 2 Hours",
            ussdCode = "*188*2*1*BH*1#",
            price = 30,
            type = OfferType.DATA
        )
    )
    RescheduleOfferScreenContent(
        onReschedule = { _, _ -> },
        onAutoRenew = { _, _ -> },
        offers = offers
    )
}