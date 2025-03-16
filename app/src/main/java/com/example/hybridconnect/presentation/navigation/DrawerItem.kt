package com.example.hybridconnect.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class DrawerItem(val title: String, val icon: ImageVector) {
    data object Home : DrawerItem("Home", Icons.Outlined.Home)
    data object Offers : DrawerItem("Offers", Icons.Outlined.LocalOffer)
    data object ForwardMessages : DrawerItem("Forward Messages", Icons.Outlined.Send)
    data object Settings : DrawerItem("Settings", Icons.Outlined.Settings)
    data object Logout : DrawerItem("Logout", Icons.AutoMirrored.Outlined.Logout)
}