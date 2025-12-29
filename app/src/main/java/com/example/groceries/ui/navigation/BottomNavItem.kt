package com.example.groceries.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    // The Inventory tab
    object Inventory : BottomNavItem(
        route = "inventory",
        title = "Inventory",
        icon = Icons.AutoMirrored.Filled.List
    )

    // The Notifications tab
    object Notifications : BottomNavItem(
        route = "notifications",
        title = "Alerts",
        icon = Icons.Filled.Notifications
    )
}
