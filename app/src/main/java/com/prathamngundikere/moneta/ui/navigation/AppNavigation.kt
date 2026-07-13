package com.prathamngundikere.moneta.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.prathamngundikere.moneta.ui.account.AccountDetailScreen
import com.prathamngundikere.moneta.ui.config.ConfigScreen
import com.prathamngundikere.moneta.ui.home.HomeScreen
import com.prathamngundikere.moneta.ui.setup.SetupScreen

@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
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
                    navController.navigate("home") {
                        popUpTo("setup") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onNavigateToAccount = { accountId ->
                    navController.navigate("accountDetail/$accountId")
                }
            )
        }

        composable(
            route = "accountDetail/{accountId}",
            arguments = listOf(navArgument("accountId") { type = NavType.StringType })
        ) {
            AccountDetailScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}