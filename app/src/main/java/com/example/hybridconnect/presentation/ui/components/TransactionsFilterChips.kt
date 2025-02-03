package com.example.hybridconnect.presentation.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hybridconnect.presentation.dto.FilterChipState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TransactionsFilterChips(
    selectedChip: FilterChipState,
    onChipSelected: (FilterChipState) -> Unit,
) {
    FlowRow(
        modifier = Modifier
            .padding(0.dp)
            .animateContentSize(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        maxItemsInEachRow = 3
    ) {
        listOf(
            FilterChipState.All,
            FilterChipState.Successful(0),
            FilterChipState.Failed(0),
            FilterChipState.Unmatched(0),
            FilterChipState.Scheduled(0),
            FilterChipState.SiteLink(0)
        ).forEach { chip ->
            val labelText = when (chip) {
                is FilterChipState.All -> "All"
                is FilterChipState.Successful -> "Successful"
                is FilterChipState.Failed -> "Failed"
                is FilterChipState.Unmatched -> "Unmatched"
                is FilterChipState.Scheduled -> "Scheduled"
                is FilterChipState.SiteLink -> "SiteLink"
            }

            FilterChip(
                selected = selectedChip == chip,
                onClick = { onChipSelected(chip) },
                label = {
                    Text(labelText)
                },
                leadingIcon = if (selectedChip == chip) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
            )
        }
    }
}