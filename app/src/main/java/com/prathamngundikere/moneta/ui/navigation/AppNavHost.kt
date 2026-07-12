package com.prathamngundikere.moneta.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.prathamngundikere.moneta.data.local.DataStoreManager
import com.prathamngundikere.moneta.ui.screens.ConfigScreen
import com.prathamngundikere.moneta.ui.screens.MainScreen
import com.prathamngundikere.moneta.ui.screens.SetupScreen
import com.prathamngundikere.moneta.ui.viewmodels.RootViewModel

@Composable
fun AppNavHost(rootViewModel: RootViewModel = hiltViewModel()) {
    val navController = rememberNavController()

    // Collect the states from the Hilt-injected ViewModel instead of a manually created DataStoreManager
    val baseUrl by rootViewModel.baseUrlFlow.collectAsState(initial = null)
    val isSetupComplete by rootViewModel.isSetupCompleteFlow.collectAsState(initial = null)

    // Show a loading spinner while we wait for DataStore
    if (isSetupComplete == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val startDestination = when {
        baseUrl.isNullOrEmpty() -> "config"
        isSetupComplete == false -> "setup"
        else -> "main"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("config") {
            ConfigScreen(
                onConfigSaved = {
                    navController.navigate("setup") {
                        popUpTo("config") { inclusive = true }
                    }
                }
            )
        }

        composable("setup") {
            SetupScreen(
                onSetupComplete = {
                    navController.navigate("main") {
                        popUpTo("setup") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            MainScreen()
        }
    }
}