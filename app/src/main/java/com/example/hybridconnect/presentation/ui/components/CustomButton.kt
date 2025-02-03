package com.example.hybridconnect.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    primary: Boolean = true,
    content: @Composable () -> Unit,
) {
    if (primary) {
        Button(
            onClick = onClick,
            modifier = modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(52.dp),
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        ) {
            content()
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            enabled = enabled,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCustomButton() {
    CustomButton(onClick = {}, primary = true) {
        Text("Custom Button")
    }
}
