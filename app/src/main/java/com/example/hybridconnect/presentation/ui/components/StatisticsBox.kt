package com.example.hybridconnect.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hybridconnect.presentation.enums.StatisticsBoxType

@Composable
fun StatisticsBox(
    label: String,
    value: String,
    boxType: StatisticsBoxType = StatisticsBoxType.SUCCESSFUL,
    onClick: () -> Unit = {},
) {
    val backgroundColor: Color = when (boxType) {
        StatisticsBoxType.SUCCESSFUL -> MaterialTheme.colorScheme.primaryContainer
        StatisticsBoxType.FAILED -> MaterialTheme.colorScheme.errorContainer
        StatisticsBoxType.TOKENS -> MaterialTheme.colorScheme.tertiaryContainer
    }
    val contentColor: Color = when (boxType) {
        StatisticsBoxType.SUCCESSFUL -> MaterialTheme.colorScheme.onPrimaryContainer
        StatisticsBoxType.FAILED -> MaterialTheme.colorScheme.onErrorContainer
        StatisticsBoxType.TOKENS -> MaterialTheme.colorScheme.onTertiaryContainer
    }

    Box(
        modifier = Modifier
            .width(100.dp)
            .height(75.dp)
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = contentColor
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = contentColor
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatisticsBoxPreview() {
    StatisticsBox(label = "Total Spent", value = "1000")
}