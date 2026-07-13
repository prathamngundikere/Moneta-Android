package com.prathamngundikere.moneta.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.prathamngundikere.moneta.R
import com.prathamngundikere.moneta.ui.home.HomeScreen
import com.prathamngundikere.moneta.ui.items.ItemsScreen

@Composable
fun DashboardScreen(
    onNavigateToAccount: (String) -> Unit,
    onNavigateToItem: (String) -> Unit
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == "accounts",
                    onClick = {
                        bottomNavController.navigate("accounts") {
                            popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(
                        painter = painterResource(R.drawable.ic_account_balance),
                        contentDescription = "Accounts"
                    ) },
                    label = { Text("Accounts") }
                )
                NavigationBarItem(
                    selected = currentRoute == "items",
                    onClick = {
                        bottomNavController.navigate("items") {
                            popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(
                        painter = painterResource(R.drawable.ic_list),
                        contentDescription = "Items"
                    ) },
                    label = { Text("Items") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "accounts",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("accounts") {
                HomeScreen(onNavigateToAccount = onNavigateToAccount)
            }
            composable("items") {
                ItemsScreen(onNavigateToItem = onNavigateToItem)
            }
        }
    }
}