package com.example.hybridconnect.presentation.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.domain.enums.AutoReplyType
import com.example.hybridconnect.domain.model.AutoReply
import com.example.hybridconnect.presentation.navigation.Route
import com.example.hybridconnect.presentation.viewmodel.AutoReplyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoReplyScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: AutoReplyViewModel = hiltViewModel(),
) {
    val autoReplies by viewModel.autoReplies.collectAsState()

    LaunchedEffect(autoReplies) {
        Log.d("AutoReps", "AutoReplies changed to $autoReplies")
    }
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("AutoReply Messages") },
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
            AutoReplyScreenContent(
                autoReplies = autoReplies,
                onActiveStatusChanged = { autoReply, status ->
                    viewModel.onAutoReplyStatusChanged(autoReply, status)
                },
                onEdit = { autoReply ->
                    navController.navigate(
                        Route.EditAutoReplyMessage.name.replace(
                            "{type}",
                            autoReply.type.name
                        )
                    )
                }
            )
        }
    }
}

@Composable
fun AutoReplyScreenContent(
    autoReplies: List<AutoReply>,
    onActiveStatusChanged: (AutoReply, Boolean) -> Unit,
    onEdit: (AutoReply) -> Unit,
) {
    LazyColumn {
        items(
            items = autoReplies,
            key = {autoReply -> autoReply.type}
        ) { autoReply ->
            AutoReplyItem(
                autoReply = autoReply,
                onActiveStatusChanged = onActiveStatusChanged,
                onEdit = onEdit
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun AutoReplyItem(
    autoReply: AutoReply,
    onActiveStatusChanged: (AutoReply, Boolean) -> Unit,
    onEdit: (AutoReply) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = autoReply.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = autoReply.message,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.tertiary,
                        fontStyle = FontStyle.Italic
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                Switch(
                    checked = autoReply.isActive,
                    onCheckedChange = { onActiveStatusChanged(autoReply, it) },
                    modifier = Modifier
                        .scale(.8f),
                )

                IconButton(
                    onClick = { onEdit(autoReply) },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier
                            .size(42.dp)
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AutoReplyScreenPreview() {
    val autoReplies = listOf(
        AutoReply(
            title = "Success AutoReply",
            type = AutoReplyType.SUCCESS,
            message = "Offer successful. Thanks for your purchase",
            isActive = true
        ),
        AutoReply(
            title = "Failed AutoReply",
            type = AutoReplyType.FAILED,
            message = "Transaction failed, please try again",
            isActive = false
        ),
        AutoReply(
            title = "Out Of Service AutoReply",
            type = AutoReplyType.OUT_OF_SERVICE,
            message = "Unfortunately out of service",
            isActive = true
        ),
        AutoReply(
            title = "Offer Unavailable",
            type = AutoReplyType.OFFER_UNAVAILABLE,
            message = "Offer unavailable. Please enter the correct amount",
            isActive = true
        ),
    )

    AutoReplyScreenContent(
        autoReplies = autoReplies,
        onActiveStatusChanged = { _, _ -> },
        onEdit = {},
    )
}