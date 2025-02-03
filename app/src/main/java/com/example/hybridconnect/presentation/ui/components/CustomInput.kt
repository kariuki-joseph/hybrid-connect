package com.example.hybridconnect.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    enabled: Boolean = true,
    textStyle: TextStyle = TextStyle.Default,
    labelStyle: TextStyle = TextStyle.Default.copy(color = MaterialTheme.colorScheme.primaryContainer),
) {
   OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier
        .fillMaxWidth(),
    enabled = enabled,
    textStyle = textStyle,
    label = { Text(text = label, style = labelStyle) }
)
}

@Preview(showBackground = true)
@Composable
fun PreviewCustomInput() {
    CustomInput(
        value = "",
        onValueChange = {},
        label = "Enter text"
    )
}