package com.example.hybridconnect.presentation.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.domain.enums.SiteLinkAccountType
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.model.SiteLink
import com.example.hybridconnect.domain.utils.SnackbarManager
import com.example.hybridconnect.presentation.navigation.Route
import com.example.hybridconnect.presentation.ui.components.CustomButton
import com.example.hybridconnect.presentation.ui.components.SiteLinkOfferItem
import com.example.hybridconnect.presentation.viewmodel.SiteLinkViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteLinkScreen(
    navController: NavHostController,
    viewModel: SiteLinkViewModel = hiltViewModel(),
    modifier: Modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
) {
    val scope = rememberCoroutineScope()
    val siteLink by viewModel.siteLink.collectAsState()
    val offers by viewModel.offers.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isSiteLinkActive by viewModel.isSiteLinkActive.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    LaunchedEffect(successMessage) {
        successMessage?.let {
            SnackbarManager.showMessage(scope, it)
            delay(2000)
            viewModel.resetSnackbarMessage()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            SnackbarManager.showMessage(scope, it)
            delay(2000)
            viewModel.resetSnackbarMessage()
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("SiteLink") }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        })
    }) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxHeight()
        ) {
            if (siteLink == null) {
                Text(
                    text = "Let your customers purchase offers online via HybridConnect SiteLink. It is via this SiteLink that they can even buy for another number",
                    style = MaterialTheme.typography.titleSmall,
                )
                Spacer(modifier = Modifier.height(8.dp))
                CustomButton(
                    primary = false,
                    modifier = Modifier.align(
                        Alignment.End
                    ),
                    onClick = {
                        navController.navigate(Route.CreateSiteLink.name)
                    },
                ) {
                    Text("Get Started")
                }
            }

            siteLink?.let { siteLink ->
                SiteLinkScreenContent(siteLink = siteLink,
                    isSiteLinkActive = isSiteLinkActive,
                    onSiteLinkActiveStatusChanged = { viewModel.onSiteLinkActiveStatusChanged(it) },
                    offers = offers,
                    onToggleSync = { offer, isLinked ->
                        if (isLinked) {
                            viewModel.removeSiteLinkOffer(offer)
                        } else {
                            viewModel.addSiteLinkOffer(offer)
                        }
                    },
                    onEditSiteLink = { navController.navigate(Route.EditSiteLink.name) }
                )
            }
        }
    }
}

@Composable
fun SiteLinkScreenContent(
    siteLink: SiteLink,
    offers: List<Offer>,
    isSiteLinkActive: Boolean,
    onSiteLinkActiveStatusChanged: (Boolean) -> Unit,
    onToggleSync: (Offer, Boolean) -> Unit,
    onEditSiteLink: (siteLink: SiteLink) -> Unit,
) {
    val context = LocalContext.current
    Column {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = siteLink.siteName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            onClick = { onEditSiteLink(siteLink) }
                        ) {
                            Text(
                                text = "Edit",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = siteLink.url,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline
                            )
                        )
                        Spacer(modifier = Modifier.weight(1F))

                        IconButton(onClick = { openCustomShareSheet(context, siteLink.url) }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        IconButton(
                            onClick = { openInBrowser(context, siteLink.url) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                                contentDescription = "Open in Browser",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Activate",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = isSiteLinkActive,
                    onCheckedChange = { onSiteLinkActiveStatusChanged(it) },
                    modifier = Modifier.scale(.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("SiteLink Offers")
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(offers) { offer ->
                SiteLinkOfferItem(offer = offer,
                    onToggleSync = { isChecked -> onToggleSync(offer, isChecked) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

private fun openCustomShareSheet(context: Context, link: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, link)
    }

    val clipboardIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, link)
        putExtra(Intent.EXTRA_TITLE, "Copy to Clipboard")
    }

    val chooserIntent = Intent.createChooser(shareIntent, "Share via")
    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(clipboardIntent))

    context.startActivity(chooserIntent)
}

fun openInBrowser(context: Context, link: String) {
    val browserIntent = Intent(Intent.ACTION_VIEW, link.toUri())
    context.startActivity(browserIntent)
}

@Preview(showBackground = true)
@Composable
private fun SiteLinkScreenPreview() {
    val siteLink = SiteLink(
        id = "2ab3cd4f",
        siteName = "Mwirigi Enterprises",
        url = "https://bingwa.co.ke/sites/f4d3a72b",
        accountType = SiteLinkAccountType.TILL,
        accountNumber = "1287"
    )
    SiteLinkScreenContent(siteLink = siteLink,
        offers = emptyList(),
        isSiteLinkActive = false,
        onSiteLinkActiveStatusChanged = { _ ->

        },
        onToggleSync = { _, _ ->

        },
        onEditSiteLink = { }
    )
}