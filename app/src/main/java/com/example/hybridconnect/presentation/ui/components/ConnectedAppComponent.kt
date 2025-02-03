package com.example.hybridconnect.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hybridconnect.domain.model.ConnectedApp

@Composable
fun ConnectedAppComponent(
    modifier: Modifier = Modifier,
    connectedApp: ConnectedApp,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = connectedApp.connectId,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.width(8.dp))
    }
}

@Composable
private fun Dot(
    isOnline: Boolean = false,
) {
    val color = if (isOnline) Color.Green else MaterialTheme.colorScheme.error
    Box(
        modifier = Modifier
            .size(8.dp)
            .background(color, CircleShape)
    )
}

@Preview(showBackground = true)
@Composable
private fun ConnectedAppComponentPreview() {
    val connectedApp = ConnectedApp(
        connectId = "BHC-6SHYS",
        isOnline = true,
        messagesSent = 30
    )
}