package com.prathamngundikere.moneta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.prathamngundikere.moneta.presentation.main.MainScreen
import com.prathamngundikere.moneta.presentation.main.MainViewModel
import com.prathamngundikere.moneta.presentation.startup.SetupScreen
import com.prathamngundikere.moneta.presentation.startup.SetupViewModel
import com.prathamngundikere.moneta.ui.theme.MonetaTheme
import dagger.hilt.android.AndroidEntryPoint

// MainActivity.kt
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MonetaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MonetaAppNavigation()
                }
            }
        }
    }
}

@Composable
fun MonetaAppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "setup") {
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
            MainScreen(mainViewModel)
        }
    }
}
