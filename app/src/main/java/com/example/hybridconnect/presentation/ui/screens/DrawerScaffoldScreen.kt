package com.example.hybridconnect.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.hybridconnect.domain.utils.Constants
import com.example.hybridconnect.presentation.navigation.DrawerItem
import com.example.hybridconnect.presentation.navigation.Route
import com.example.hybridconnect.presentation.ui.components.CustomTopAppBar
import kotlinx.coroutines.launch

@Composable
fun DrawerScaffoldScreen(
    navController: NavHostController,
    topBarTitle: String,
    onLogout: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }

    val items = listOf(
        DrawerItem.Home,
        DrawerItem.Settings,
    )

    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        AppDrawer(
            items = items,
            navController = navController,
            closeDrawer = {
                scope.launch {
                    drawerState.close()
                }
            },
            onLogout = {
                showLogoutDialog = true
            },
        )
    }, content = {
        Scaffold(
            topBar = {
                CustomTopAppBar(
                    topBarTitle = topBarTitle,
                    onMenuClick = { scope.launch { drawerState.open() } },
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) { content() }
        }
    })

    LogoutConfirmationDialog(
        showDialog = showLogoutDialog,
        isLoggingOut = false,
        onConfirm = {
            showLogoutDialog = false
            onLogout()
        },
        onDismiss = { showLogoutDialog = false })
}

@Composable
fun AppDrawer(
    items: List<DrawerItem>,
    navController: NavHostController,
    closeDrawer: () -> Unit,
    onLogout: () -> Unit,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    ModalDrawerSheet(
        modifier = Modifier.width(screenWidth / 1.5f),
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Text(
                text = Constants.APP_NAME,
                style = androidx.compose.material3.MaterialTheme.typography.headlineLarge,
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
            ) {
                items(items) { item ->
                    NavigationDrawerItem(label = item.title, icon = item.icon, onClick = {
                        handleDrawerItemClick(item, navController, closeDrawer)
                    })
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider()
            NavigationDrawerItem(
                label = DrawerItem.Logout.title,
                icon = DrawerItem.Logout.icon,
                onClick = onLogout
            )
        }
    }
}

@Composable
fun LogoutConfirmationDialog(
    showDialog: Boolean,
    isLoggingOut: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (showDialog) {
        AlertDialog(onDismissRequest = onDismiss, title = {
            Text(text = "Confirm Logout")
        }, text = {
            Text(text = "Are you sure you want to log out?")
        }, confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = if (isLoggingOut) "Logging out.." else "Log out")
            }
        }, dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        })
    }
}


@Composable
fun NavigationDrawerItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }
        .padding(16.dp)) {
        Icon(imageVector = icon, contentDescription = null)
        Text(
            text = label, modifier = Modifier.padding(start = 16.dp)
        )
    }
}


fun handleDrawerItemClick(
    drawerItem: DrawerItem,
    navController: NavHostController,
    closeDrawer: () -> Unit,
) {
    when (drawerItem) {
        is DrawerItem.Home -> {
            navController.navigate(Route.Home.name)
        }

        is DrawerItem.Settings -> {
            navController.navigate(Route.Settings.name)
        }

        else -> {
            navController.navigate(Route.Home.name)
        }
    }
    // Close the drawer after navigation
    closeDrawer()
}
