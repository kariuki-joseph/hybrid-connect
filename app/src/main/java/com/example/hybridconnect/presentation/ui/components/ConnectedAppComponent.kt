package com.example.hybridconnect.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
    onDeleteApp: (app: ConnectedApp) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = connectedApp.connectId,
            style = MaterialTheme.typography.bodyMedium
        )
       Text(text = "-")
        Text(
            text = connectedApp.appName,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.width(8.dp))

        Dot(isOnline = connectedApp.isOnline)

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = connectedApp.messagesSent.toString(),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = { onDeleteApp(connectedApp) }
        ){
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Delete App"
            )
        }
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
        appName = "Hybrid Main",
        isOnline = true,
        messagesSent = 30,
    )

    ConnectedAppComponent(
        connectedApp = connectedApp,
        onDeleteApp = {}
    )
}