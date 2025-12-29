package com.example.groceries.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.groceries.ui.screens.*

//the feature for switching tabs (NavHost)
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavBar(navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Inventory.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Inventory.route) {
                InventoryScrn()
            }
            composable(BottomNavItem.Notifications.route) {
                NotificationsScrn()
            }
        }
    }
}
