package com.example.hybridconnect.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hybridconnect.presentation.theme.spacing

@Composable
fun SingleLineTextField(
    intervalValue: String,
    onValueChange: (String) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(MaterialTheme.spacing.extraLarge)
            .padding(bottom = MaterialTheme.spacing.extraSmall)
    ) {
        BasicTextField(
            value = intervalValue,
            onValueChange = { value ->
                onValueChange(value.filter { it.isDigit() })
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center // Center the text horizontally
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary), // Material Design cursor
            modifier = Modifier
                .fillMaxWidth()
        )
        // Underline
        Box(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) // Material Design underline
                .align(Alignment.BottomStart)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SingeLineTextFieldPreview() {
    SingleLineTextField(
        intervalValue = "2",
        onValueChange = {}
    )
}