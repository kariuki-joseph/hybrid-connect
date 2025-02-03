package com.example.hybridconnect.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.hybridconnect.R

@Composable
fun PaymentSuccessDialog(
    message: String,
    onDismiss: () -> Unit,
    onFinishClick: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .wrapContentWidth()
                .wrapContentHeight()
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                tonalElevation = AlertDialogDefaults.TonalElevation,
                modifier = Modifier.padding(top = 32.dp) // Space for the check icon
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .align(Alignment.CenterHorizontally)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_check),
                            contentDescription = "Success",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp)) // Offset for the check circle
                    Text(
                        text = "Success",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textAlign = TextAlign.Center
                        ),
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    CustomButton(
                        onClick = onFinishClick,
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        Text(
                            text = "Finish",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

        }
    }
}

@Preview
@Composable
private fun PaymentSuccessDialogPreview() {
    PaymentSuccessDialog(
        message = "Your subscription will be added automatically upon successful payment",
        onDismiss = {},
        onFinishClick = {}
    )
}