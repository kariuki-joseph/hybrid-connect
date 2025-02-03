package com.example.hybridconnect.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Keypad(
    onNumberClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onFingerprintClick: () -> Unit = {},
    pin: String = "",
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        for (row in listOf("123", "456", "789", " 0D")) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                for (char in row) {
                    if (char == ' ') {
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                        )
                    } else {
                        Button(
                            onClick = {
                                if (char == 'D') {
                                    if (pin.isNotEmpty()) {
                                        onDeleteClick()
                                    } else {
                                        onFingerprintClick()
                                    }
                                } else {
                                    onNumberClick(char.toString())
                                }
                            },
                            modifier = Modifier
                                .weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RectangleShape
                        ) {
                            if (char == 'D') {
                                Icon(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .align(Alignment.CenterVertically),
                                    imageVector = if (pin.isNotEmpty()) Icons.AutoMirrored.Outlined.Backspace else Icons.Outlined.Fingerprint,
                                    contentDescription = if (pin.isNotEmpty()) "Backspace" else "Fingerprint",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            } else {
                                Text(
                                    text = char.toString(),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Light)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun KeypadPreview() {
    Keypad(onNumberClick = {}, onDeleteClick = {}, onFingerprintClick = {}, pin = "12")
}
