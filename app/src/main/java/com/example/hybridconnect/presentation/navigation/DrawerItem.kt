package com.example.hybridconnect.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ForwardToInbox
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.CellTower
import androidx.compose.material.icons.outlined.CellWifi
import androidx.compose.material.icons.outlined.Dialpad
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LeakAdd
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.ui.graphics.vector.ImageVector

sealed class DrawerItem(val title: String, val icon: ImageVector) {
    data object Home : DrawerItem("Home", Icons.Outlined.Home)
    data object Offers : DrawerItem("Offers", Icons.Outlined.LocalOffer)
    data object Customers : DrawerItem("Customer Balances", Icons.Outlined.People)
    data object Settings : DrawerItem("Settings", Icons.Outlined.Settings)
    data object QuickDial : DrawerItem("Quick Dial", Icons.Outlined.Dialpad)
    data object SiteLink : DrawerItem("Site Link", Icons.Outlined.Link)
    data object AutoReply : DrawerItem("AutoReplies", Icons.AutoMirrored.Outlined.ForwardToInbox)
    data object Subscription : DrawerItem("Subscriptions", Icons.Outlined.Subscriptions)
    data object HybridConnect : DrawerItem("Hybrid Connect", Icons.Outlined.LeakAdd)
    data object Logout : DrawerItem("Logout", Icons.AutoMirrored.Outlined.Logout)
}