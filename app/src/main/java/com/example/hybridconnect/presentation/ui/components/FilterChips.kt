package com.example.hybridconnect.presentation.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.hybridconnect.presentation.dto.FilterChipState
import com.example.hybridconnect.presentation.theme.spacing
import androidx.compose.material3.FilterChip as FilterChip1

@Composable
fun FilterChips(
    selectedChip: FilterChipState,
    onChipSelected: (FilterChipState) -> Unit,
    successCount: Int = 0,
    failedCount: Int = 0,
    unmatchedCount: Int = 0,
    scheduledCount: Int = 0,
    siteLinkCount: Int = 0,
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
            FilterChipState.All,
            FilterChipState.Successful(successCount),
            FilterChipState.Failed(failedCount),
            FilterChipState.Unmatched(unmatchedCount),
            FilterChipState.Scheduled(scheduledCount),
            FilterChipState.SiteLink(siteLinkCount)
        ).forEach { chip ->
            val labelText = when (chip) {
                is FilterChipState.All -> "All"
                is FilterChipState.Successful -> "Successful (${chip.count})"
                is FilterChipState.Failed -> "Failed (${chip.count})"
                is FilterChipState.Unmatched -> "Unmatched (${chip.count})"
                is FilterChipState.Scheduled -> "Scheduled (${chip.count})"
                is FilterChipState.SiteLink -> "SiteLink (${chip.count})"
            }

            FilterChip1(selected = selectedChip == chip,
                onClick = { onChipSelected(chip) },
                label = { Text(labelText) })
        }
    }
}