package com.prathamngundikere.moneta.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.prathamngundikere.moneta.ui.viewmodels.SetupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(viewModel: SetupViewModel = hiltViewModel(), onSetupComplete: () -> Unit) {
    var selectedCode by remember { mutableStateOf("") }
    var accName by remember { mutableStateOf("") }
    var accBalance by remember { mutableStateOf("") }

    LaunchedEffect(viewModel.setupComplete) {
        if (viewModel.setupComplete) onSetupComplete()
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Setup System", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            // Simplified Dropdown for currency
            OutlinedTextField(
                value = selectedCode,
                onValueChange = { selectedCode = it },
                label = { Text("Currency Code (e.g., USD)") },
                modifier = Modifier.fillMaxWidth()
            )

            Divider(Modifier.padding(vertical = 16.dp))

            Text("Add Initial Accounts", style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = accName, onValueChange = { accName = it },
                    label = { Text("Name") }, modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = accBalance, onValueChange = { accBalance = it },
                    label = { Text("Balance") }, modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Button(
                onClick = {
                    viewModel.addAccountDraft(accName, "ASSET", accBalance.toDoubleOrNull() ?: 0.0)
                    accName = ""
                    accBalance = ""
                },
                modifier = Modifier.align(Alignment.End).padding(top = 8.dp)
            ) {
                Text("Add to list")
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(viewModel.accountsToCreate) { acc ->
                    ListItem(
                        headlineContent = { Text(acc.name) },
                        trailingContent = { Text("${acc.balance}") }
                    )
                }
            }

            // --- NEW: Error Message Display ---
            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // --- UPDATED: Button with Loading State ---
            Button(
                onClick = { viewModel.finalizeSetup(selectedCode, "$") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewModel.isLoading // Disable button while processing
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Complete Setup")
                }
            }
        }
    }
}