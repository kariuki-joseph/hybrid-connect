package com.example.hybridconnect.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val MaterialTheme.spacing: Spacing
    @Composable
    get() = Spacing()

// Define spacing values for common use cases
data class Spacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val tiny: Dp = 2.dp,
    val default: Dp = 12.dp,
    val screenPadding: Dp = 20.dp
)
