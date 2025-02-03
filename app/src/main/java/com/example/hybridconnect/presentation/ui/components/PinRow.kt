package com.example.hybridconnect.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun PinRow(
    modifier: Modifier = Modifier,
    pin: String,
    isLoading: Boolean = false,
    hasError: Boolean = false,
) {

    var animatedPin by remember { mutableStateOf("") }

    LaunchedEffect(pin) {
        animatedPin = pin
    }

    LaunchedEffect(isLoading) {
        while (isLoading) {
            for (i in 0..4) {
                animatedPin = pin.take(i)
                delay(100)
            }
            animatedPin = ""
        }
    }

    Row(
        modifier = modifier
    ) {
        for (i in 0 until 4) {
            val color by animateColorAsState(
                targetValue = when {
                    hasError -> MaterialTheme.colorScheme.error
                    i < animatedPin.length -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                },
                label = "PinRowColor"
            )

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(4.dp)
                    .background(
                        color = color,
                        shape = CircleShape
                    )
            )
            if (i < 3) {
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PinRowPreview() {
    PinRow(pin = "123", isLoading = true)
}