package com.prathamngundikere.moneta.presentation.startup

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prathamngundikere.moneta.R
import com.prathamngundikere.moneta.ui.theme.MonetaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    onConnectClicked: (serverUrl: String, apiKey: String) -> Unit
) {
    // State variables for our input fields
    var serverUrl by remember { mutableStateOf("http://localhost:8080") }
    var apiKey by remember { mutableStateOf("") }
    var isApiKeyVisible by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo or Header Icon
            Icon(
                painter = painterResource(id = R.drawable.ic_moneta_logo),
                contentDescription = "Moneta Logo",
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Screen Titles
            Text(
                text = "Welcome to Moneta",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Connect to your personal finance ledger to get started.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Server URL Input
            OutlinedTextField(
                value = serverUrl,
                onValueChange = { serverUrl = it },
                label = { Text("Server URL") },
                placeholder = { Text("http://localhost:8080") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_server),
                        contentDescription = "Server Icon"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // API Key Input
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("X-API-KEY") },
                placeholder = { Text("Enter your API Key") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_password),
                        contentDescription = "API Key Icon"
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { isApiKeyVisible = !isApiKeyVisible }) {
                        Icon(
                            painter = if (isApiKeyVisible) {
                                painterResource(id = R.drawable.ic_visibility)
                            } else {
                                painterResource(id = R.drawable.ic_visibility_off)
                            },
                            contentDescription = "Toggle API Key Visibility"
                        )
                    }
                },
                visualTransformation = if (isApiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Connect Button
            Button(
                onClick = { onConnectClicked(serverUrl, apiKey) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = serverUrl.isNotBlank() && apiKey.isNotBlank(),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = "Connect",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Preview(
    name = "Setup Screen - Light Mode",
    showBackground = true,
    showSystemUi = true
)
@Preview(
    name = "Setup Screen - Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    showSystemUi = true
)
@Composable
fun SetupScreenPreview() {
    // Wrapping in MaterialTheme to ensure M3 default colors/typography apply
    // even if you haven't fully set up your custom MonetaTheme yet.
    MonetaTheme {
        Surface {
            SetupScreen(
                onConnectClicked = { serverUrl, apiKey ->
                    // Dummy action for preview purposes
                    println("Preview Connect Clicked: $serverUrl, Key: $apiKey")
                }
            )
        }
    }
}