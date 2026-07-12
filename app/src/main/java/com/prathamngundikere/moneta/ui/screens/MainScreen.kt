package com.prathamngundikere.moneta.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.prathamngundikere.moneta.ui.viewmodels.MainViewModel
import kotlinx.coroutines.launch
import com.prathamngundikere.moneta.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(mainViewModel: MainViewModel = hiltViewModel()) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentMainRoute by remember { mutableStateOf("transactions") }
    var currentDrawerRoute by remember { mutableStateOf<String?>(null) }

    // Nuke state
    var showNukeWarning by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(24.dp))
                Text("Management", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                NavigationDrawerItem(
                    label = { Text("Categories") },
                    selected = currentDrawerRoute == "categories",
                    onClick = { currentDrawerRoute = "categories"; scope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    label = { Text("Items Master List") },
                    selected = currentDrawerRoute == "items",
                    onClick = { currentDrawerRoute = "items"; scope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    label = { Text("Recurring Templates") },
                    selected = currentDrawerRoute == "recurring",
                    onClick = { currentDrawerRoute = "recurring"; scope.launch { drawerState.close() } }
                )
                HorizontalDivider(
                    Modifier.padding(vertical = 8.dp),
                    DividerDefaults.Thickness,
                    DividerDefaults.color
                )
                NavigationDrawerItem(
                    label = { Text("DANGER: Nuke System", color = MaterialTheme.colorScheme.error) },
                    selected = false,
                    onClick = { showNukeWarning = true; scope.launch { drawerState.close() } }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentDrawerRoute?.replaceFirstChar { it.uppercase() } ?: "Dashboard") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(painter = painterResource(R.drawable.ic_menu), contentDescription = "Menu")
                        }
                    },
                    actions = {
                        Icon(
                            painter = painterResource(R.drawable.ic_circle) ,
                            contentDescription = "Server Status",
                            tint = if (mainViewModel.isServerOnline) Color.Green else Color.Red,
                            modifier = Modifier.padding(end = 16.dp).size(12.dp)
                        )
                    }
                )
            },
            bottomBar = {
                // Hide bottom bar if we are in a drawer management screen
                if (currentDrawerRoute == null) {
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Icon(painter = painterResource(R.drawable.ic_list), contentDescription = "Transactions") },
                            label = { Text("Transactions") },
                            selected = currentMainRoute == "transactions",
                            onClick = { currentMainRoute = "transactions" }
                        )
                        NavigationBarItem(
                            icon = { Icon(painter = painterResource(R.drawable.ic_account_balance), contentDescription = "Accounts") },
                            label = { Text("Accounts") },
                            selected = currentMainRoute == "accounts",
                            onClick = { currentMainRoute = "accounts" }
                        )
                    }
                }
            },
            floatingActionButton = {
                if (currentDrawerRoute == null) {
                    FloatingActionButton(onClick = { /* Open Transaction/Account FAB Menu */ }) {
                        Icon(painterResource(R.drawable.ic_add), contentDescription = "Add")
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                // Routing logic
                if (currentDrawerRoute != null) {
                    // Back handler to exit drawer routes and return to main dashboard
                    BackHandler { currentDrawerRoute = null }

                    when (currentDrawerRoute) {
                        "categories" -> CategoriesScreen()
                        "items" -> ItemsScreen()
                        "recurring" -> RecurringTransactionsScreen()
                    }
                } else {
                    when (currentMainRoute) {
                        "transactions" -> TransactionsScreen()
                        "accounts" -> AccountsScreen()
                    }
                }
            }
        }
    }

    // Nuke Warning Dialogs
    if (showNukeWarning) {
        AlertDialog(
            onDismissRequest = { showNukeWarning = false },
            title = { Text("WIPE SERVER") },
            text = { Text("This will permanently delete all transactions, items, and accounts. Proceed?") },
            confirmButton = {
                TextButton(onClick = {
                    mainViewModel.nukeSystem { showNukeWarning = false }
                }) { Text("NUKE", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showNukeWarning = false }) { Text("Cancel") }
            }
        )
    }
}