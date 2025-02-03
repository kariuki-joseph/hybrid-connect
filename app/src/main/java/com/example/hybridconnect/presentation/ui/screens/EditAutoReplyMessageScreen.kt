package com.example.hybridconnect.presentation.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.domain.enums.AutoReplyType
import com.example.hybridconnect.presentation.ui.components.CustomButton
import com.example.hybridconnect.presentation.viewmodel.AutoReplyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAutoReplyMessageScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: AutoReplyViewModel = hiltViewModel(),
    autoReplyType: AutoReplyType
) {
    val autoReplyMessage by viewModel.autoReplyMessage.collectAsState()
    val updateSuccess by viewModel.updateSuccess.collectAsState()

    LaunchedEffect(autoReplyType) {
        viewModel.getAutoReplyByType(autoReplyType)
    }

    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            navController.popBackStack()
            viewModel.resetSuccessStatus()
        }
    }


    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Edit AutoReply Message") },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )
    }) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            EditAutoReplyMessageScreenContent(
                message = autoReplyMessage,
                onMessageChanged = { viewModel.onAutoReplyMessageChanged(it) },
                onSave = { viewModel.updateReplyMessage() }
            )
        }
    }
}

@Composable
private fun EditAutoReplyMessageScreenContent(
    message: String,
    onMessageChanged: (String) -> Unit,
    onSave: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .imePadding()
            .verticalScroll(scrollState)
    ) {
        Text(text = "PlaceHolders: ")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            ) {
                append("<firstName>")
            }
            withStyle(
                style = SpanStyle(
                    fontStyle = FontStyle.Italic,
                    fontSize = 12.sp
                )
            ) {
                append(" - Customer's first name, ")
            }
        })
        Text(text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            ) {
                append("<surname>")
            }
            withStyle(
                style = SpanStyle(
                    fontStyle = FontStyle.Italic,
                    fontSize = 12.sp
                )
            ) {
                append(" - Customer's surname, ")
            }
        })

        Text(text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            ) {
                append("<amount>")
            }
            withStyle(
                style = SpanStyle(
                    fontStyle = FontStyle.Italic,
                    fontSize = 12.sp
                )
            ) {
                append(" - Amount paid by the customer")
            }
        })

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = message,
            onValueChange = { onMessageChanged(it) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences
            ),
            label = { Text("Reply Message") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 160.dp),
            textStyle = TextStyle(
                fontSize = 18.sp,
                lineHeight = 32.sp,
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomButton(
            primary = true,
            onClick = {
                keyboardController?.hide()
                onSave()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Update",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditAutoReplyMessageScreenPreview() {
    EditAutoReplyMessageScreenContent(
        message = "Thank you for your purchase",
        onMessageChanged = {},
        onSave = {},
    )
}