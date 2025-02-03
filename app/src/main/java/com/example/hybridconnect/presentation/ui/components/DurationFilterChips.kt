package com.example.hybridconnect.presentation.ui.components

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
import com.example.hybridconnect.presentation.dto.DurationFilter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DurationFilterChips(
    selectedChip: DurationFilter?,
    onChipSelected: (DurationFilter) -> Unit,
) {
    FlowRow(
        modifier = Modifier
            .padding(0.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        listOf(
            DurationFilter.Today,
            DurationFilter.Yesterday,
            DurationFilter.Last7Days,
            DurationFilter.Last30Days,
        ).forEach { chip ->
            val labelText = when (chip) {
                is DurationFilter.Today -> "Today"
                is DurationFilter.Yesterday -> "Yesterday"
                is DurationFilter.Last7Days -> "Last 7 days"
                is DurationFilter.Last30Days -> "Last 30 days"
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