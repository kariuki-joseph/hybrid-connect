package com.example.hybridconnect.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hybridconnect.domain.model.ConnectedApp

@Composable
fun ConnectedAppComponent(
    connectedApp: ConnectedApp,
    queueSize: Int,
    isDeletingApp: Boolean = false,
    onDeleteApp: (app: ConnectedApp) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = buildAnnotatedString {
                append(connectedApp.connectId)
                append(" ")
                withStyle(style = SpanStyle(
                    fontSize = 11.sp
                )){
                    append("(${connectedApp.appName})")
                }
                connectedApp.connectId
            },
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.width(8.dp))

        Dot(isOnline = connectedApp.isOnline)

        Spacer(modifier = Modifier.width(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(
                        fontWeight = FontWeight.Bold
                    )){
                        append(queueSize.toString())
                    }
                    append(" ... ")
                    withStyle(style = SpanStyle(
                        fontWeight = FontWeight.Bold
                    )){
                        append(connectedApp.messagesSent.toString())
                    }
                },
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.secondary
                )
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Sent Messages",
                modifier = Modifier.size(12.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = { onDeleteApp(connectedApp) }
        ) {
            if (isDeletingApp) {
                CircularProgressIndicator()
            } else {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete App",
                    modifier = Modifier.size(24.dp)
                )
            }
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
            .size(10.dp)
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
        queueSize = 10,
        onDeleteApp = {}
    )
}