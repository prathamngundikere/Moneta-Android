package com.prathamngundikere.moneta.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.prathamngundikere.moneta.ui.account.AccountDetailScreen
import com.prathamngundikere.moneta.ui.config.ConfigScreen
import com.prathamngundikere.moneta.ui.main.DashboardScreen
import com.prathamngundikere.moneta.ui.setup.SetupScreen
import com.prathamngundikere.moneta.ui.items.ItemDetailScreen

@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()

    // If startDestination is "home" from MainViewModel, we route it to "dashboard"
    val actualStart = if (startDestination == "home") "dashboard" else startDestination

    NavHost(
        navController = navController,
        startDestination = actualStart
    ) {

        composable("config") {
            ConfigScreen(
                onComplete = {
                    navController.navigate("setup") {
                        popUpTo("config") { inclusive = true }
                    }
                }
            )
        }

        composable("setup") {
            SetupScreen(
                onComplete = {
                    navController.navigate("dashboard") {
                        popUpTo("setup") { inclusive = true }
                    }
                }
            )
        }

        // Replaced "home" with "dashboard" to host the Bottom Navigation UI
        composable("dashboard") {
            DashboardScreen(
                onNavigateToAccount = { accountId ->
                    navController.navigate("accountDetail/$accountId")
                },
                onNavigateToItem = { itemId ->
                    navController.navigate("itemDetail/$itemId")
                }
            )
        }

        composable(
            route = "accountDetail/{accountId}",
            arguments = listOf(navArgument("accountId") { type = NavType.StringType })
        ) {
            AccountDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // New Detail Route for Items
        composable(
            route = "itemDetail/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) {
            ItemDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}