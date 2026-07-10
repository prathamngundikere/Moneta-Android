package com.prathamngundikere.moneta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.prathamngundikere.moneta.presentation.SplashViewModel
import com.prathamngundikere.moneta.presentation.add_account.AddAccountScreen
import com.prathamngundikere.moneta.presentation.add_account.AddAccountViewModel
import com.prathamngundikere.moneta.presentation.add_category.AddCategoryScreen
import com.prathamngundikere.moneta.presentation.add_category.AddCategoryViewModel
import com.prathamngundikere.moneta.presentation.add_item.AddItemScreen
import com.prathamngundikere.moneta.presentation.add_item.AddItemViewModel
import com.prathamngundikere.moneta.presentation.add_transaction.AddTransactionScreen
import com.prathamngundikere.moneta.presentation.add_transaction.AddTransactionViewModel
import com.prathamngundikere.moneta.presentation.main.MainScreen
import com.prathamngundikere.moneta.presentation.main.MainViewModel
import com.prathamngundikere.moneta.presentation.startup.SetupScreen
import com.prathamngundikere.moneta.presentation.startup.SetupViewModel
import com.prathamngundikere.moneta.presentation.transfer.TransferScreen
import com.prathamngundikere.moneta.ui.theme.MonetaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MonetaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val splashViewModel = hiltViewModel<SplashViewModel>()
                    val isLoading by splashViewModel.isLoading.collectAsState()
                    val startDestination by splashViewModel.startDestination.collectAsState()

                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        MonetaAppNavigation(startDestination)
                    }
                }
            }
        }
    }
}

@Composable
fun MonetaAppNavigation(startDestination: String) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable("setup") {
            val setupViewModel = hiltViewModel<SetupViewModel>()
            SetupScreen(
                onConnectClicked = { url, apiKey ->
                    setupViewModel.connect(url, apiKey) {
                        navController.navigate("main") {
                            popUpTo("setup") { inclusive = true }
                        }
                    }
                }
            )
        }
        composable("main") {
            val mainViewModel = hiltViewModel<MainViewModel>()
            MainScreen(
                viewModel = mainViewModel,
                onNavigateToAddAccount = { navController.navigate("addAccount") },
                onNavigateToAddCategory = { navController.navigate("addCategory") },
                onNavigateToAddItem = { navController.navigate("addItem") },
                onNavigateToTransfer = { navController.navigate("transfer") },
                onNavigateToAddTransaction = { navController.navigate("addTransaction") }
            )
        }
        composable("addAccount") {
            val vm = hiltViewModel<AddAccountViewModel>()
            AddAccountScreen(viewModel = vm, onNavigateBack = { navController.popBackStack() })
        }
        composable("addCategory") {
            val vm = hiltViewModel<AddCategoryViewModel>()
            AddCategoryScreen(viewModel = vm, onNavigateBack = { navController.popBackStack() })
        }
        composable("addItem") {
            val vm = hiltViewModel<AddItemViewModel>()
            AddItemScreen(viewModel = vm, onNavigateBack = { navController.popBackStack() })
        }
        composable("transfer") {
            val vm = hiltViewModel<AddTransactionViewModel>()
            TransferScreen(viewModel = vm, onNavigateBack = { navController.popBackStack() })
        }
        composable("addTransaction") {
            val vm = hiltViewModel<AddTransactionViewModel>()
            AddTransactionScreen(viewModel = vm, onNavigateBack = { navController.popBackStack() })
        }
    }
}