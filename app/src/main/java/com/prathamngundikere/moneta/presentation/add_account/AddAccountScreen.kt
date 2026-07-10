package com.prathamngundikere.moneta.presentation.add_account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.prathamngundikere.moneta.R
import com.prathamngundikere.moneta.core.Resource
import com.prathamngundikere.moneta.domain.model.AccountType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountScreen(
    viewModel: AddAccountViewModel,
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(AccountType.ASSET) }
    var balance by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("USD") }
    var expanded by remember { mutableStateOf(false) }

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state) {
        if (state is Resource.Success) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Account") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(painterResource(R.drawable.ic_close), contentDescription = "Close")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state is Resource.Error) {
                Text(
                    text = state?.message ?: "An unknown error occurred. Ensure server URL is correct (use 10.0.2.2 instead of localhost for emulator).",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Account Name") },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = selectedType.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Account Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    AccountType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = {
                                selectedType = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = balance,
                onValueChange = { balance = it },
                label = { Text("Starting Balance") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = currency,
                onValueChange = { currency = it },
                label = { Text("Currency (e.g. USD, INR)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val bal = balance.toDoubleOrNull() ?: 0.0
                    viewModel.createAccount(name, selectedType, bal, currency)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && state !is Resource.Loading
            ) {
                if (state is Resource.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Save Account")
                }
            }
        }
    }
}