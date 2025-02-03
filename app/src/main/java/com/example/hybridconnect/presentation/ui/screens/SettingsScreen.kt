package com.example.hybridconnect.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.SimCard
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.utils.SnackbarManager
import com.example.hybridconnect.presentation.navigation.Route
import com.example.hybridconnect.presentation.ui.components.ProfileScreenContent
import com.example.hybridconnect.presentation.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    navController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val agent by viewModel.agent.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val settings by viewModel.settings.collectAsState()

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            SnackbarManager.showMessage(scope, it)
            viewModel.resetSnackbarMessage()
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Settings") },
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
            modifier = modifier.padding(paddingValues),
            horizontalAlignment = Alignment.Start
        ) {
            agent?.let {
                ProfileScreenContent(
                    agent = it,
                    onEditProfile = {
                        navController.navigate(Route.EditProfile.name)
                    },
                )
            }

            SettingsScreenContent(
                settings = settings,
                onSettingChange = { setting, isChecked ->
                    viewModel.updateSetting(setting, isChecked)
                }
            )
        }
    }
}

@Composable
fun SettingsScreenContent(
    settings: Map<AppSetting, Boolean>,
    onSettingChange: (AppSetting, Boolean) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        SectionTitle("SIM Setup")
        Text(
            text = "SIM to receive payments",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        SettingItem {
            SwitchSettingItem(
                setting = AppSetting.RECEIVE_PAYMENTS_VIA_SIM_1,
                title = "SIM 1",
                isChecked = settings[AppSetting.RECEIVE_PAYMENTS_VIA_SIM_1] ?: false,
                onCheckedChange = onSettingChange,
                icon = { Icon(Icons.Outlined.SimCard, contentDescription = "SIM") }
            )
        }
        SettingItem {
            SwitchSettingItem(
                setting = AppSetting.RECEIVE_PAYMENTS_VIA_SIM_2,
                title = "SIM 2",
                isChecked = settings[AppSetting.RECEIVE_PAYMENTS_VIA_SIM_2] ?: false,
                onCheckedChange = onSettingChange,
                icon = { Icon(Icons.Outlined.SimCard, contentDescription = "SIM") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "HybridConnect SIM (To run USSDs)",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        SettingItem {
            SwitchSettingItem(
                setting = AppSetting.DIAL_USSD_VIA_SIM_1,
                title = "SIM 1",
                isChecked = settings[AppSetting.DIAL_USSD_VIA_SIM_1] ?: false,
                onCheckedChange = onSettingChange,
                icon = { Icon(Icons.Outlined.SimCard, contentDescription = "SIM") }
            )
        }
        SettingItem {
            SwitchSettingItem(
                setting = AppSetting.DIAL_USSD_VIA_SIM_2,
                title = "SIM 2",
                isChecked = settings[AppSetting.DIAL_USSD_VIA_SIM_2] ?: false,
                onCheckedChange = onSettingChange,
                icon = { Icon(Icons.Outlined.SimCard, contentDescription = "SIM") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        SectionTitle("Message Processing")
        SettingItem {
            SwitchSettingItem(
                setting = AppSetting.PROCESS_MPESA_MESSAGES,
                title = "Process M-Pesa Messages",
                isChecked = settings[AppSetting.PROCESS_MPESA_MESSAGES] ?: false,
                onCheckedChange = onSettingChange
            )
        }

        SettingItem {
            SwitchSettingItem(
                setting = AppSetting.PROCESS_TILL_MESSAGES,
                title = "Process Till Messages",
                isChecked = settings[AppSetting.PROCESS_TILL_MESSAGES] ?: false,
                onCheckedChange = onSettingChange
            )
        }

        SettingItem {
            SwitchSettingItem(
                setting = AppSetting.AUTO_RETRY_PENDING_REQUESTS,
                title = "Auto-Retry Pending Requests",
                isChecked = settings[AppSetting.AUTO_RETRY_PENDING_REQUESTS] ?: false,
                onCheckedChange = onSettingChange
            )
        }

        SettingItem {
            SwitchSettingItem(
                setting = AppSetting.AUTO_RETRY_SERVICE_UNAVAILABLE_RESPONSE,
                title = "Smart Auto-Retry - SMS Only",
                isChecked = settings[AppSetting.AUTO_RETRY_SERVICE_UNAVAILABLE_RESPONSE] ?: false,
                onCheckedChange = onSettingChange
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall.copy(
            color = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
fun SettingItem(
    content: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(vertical = 2.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
fun SwitchSettingItem(
    setting: AppSetting,
    title: String,
    isChecked: Boolean,
    onCheckedChange: (AppSetting, Boolean) -> Unit,
    icon: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let { it() }
        Text(
            text = title,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )
        Switch(
            checked = isChecked,
            onCheckedChange = { onCheckedChange(setting, it) },
            modifier = Modifier
            .scale(.8f),
        )
    }
}


@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    val settings = mapOf<AppSetting, Boolean>()
    SettingsScreenContent(
        settings = settings,
        onSettingChange = { setting, isChecked -> },
    )
}