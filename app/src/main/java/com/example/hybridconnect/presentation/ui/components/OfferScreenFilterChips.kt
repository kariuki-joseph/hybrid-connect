package com.example.hybridconnect.presentation.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hybridconnect.domain.enums.OfferType
import com.example.hybridconnect.presentation.theme.spacing

@Composable
fun OfferScreenFilterChips(
    onOfferTypeSelected: (OfferType) -> Unit,
    selectedOfferType: OfferType,
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(MaterialTheme.spacing.small),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {

        listOf(
            OfferType.NONE,
            OfferType.DATA,
            OfferType.VOICE,
            OfferType.SMS,
        ).forEach { offer ->
            val labelText = when (offer) {
                OfferType.NONE -> "All"
                OfferType.DATA -> "Data"
                OfferType.VOICE -> "Airtime"
                OfferType.SMS -> "SMS"
            }

            FilterChip(
                selected = offer == selectedOfferType,
                label = { Text(labelText) },
                onClick = { onOfferTypeSelected(offer) },
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OfferScreenFilterChipsPreview() {
    OfferScreenFilterChips(
        onOfferTypeSelected = {},
        selectedOfferType = OfferType.NONE
    )
}