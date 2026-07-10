package com.prathamngundikere.moneta.presentation.transfer

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
import com.prathamngundikere.moneta.data.remote.dto.TransactionSplitDto
import com.prathamngundikere.moneta.domain.model.Account
import com.prathamngundikere.moneta.presentation.add_transaction.AddTransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    viewModel: AddTransactionViewModel, // Reusing ViewModel since logic is similar
    onNavigateBack: () -> Unit
) {
    val accounts by viewModel.accounts.collectAsState()
    val state by viewModel.uiState.collectAsState()

    var sourceAccount by remember { mutableStateOf<Account?>(null) }
    var destAccount by remember { mutableStateOf<Account?>(null) }
    var amount by remember { mutableStateOf("") }

    var sourceExpanded by remember { mutableStateOf(false) }
    var destExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        if (state is Resource.Success) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transfer Funds") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(painterResource(R.drawable.ic_close), contentDescription = "Close") }
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
            ExposedDropdownMenuBox(expanded = sourceExpanded, onExpandedChange = { sourceExpanded = it }) {
                OutlinedTextField(
                    value = sourceAccount?.name ?: "From Account",
                    onValueChange = {}, readOnly = true, label = { Text("Source") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sourceExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = sourceExpanded, onDismissRequest = { sourceExpanded = false }) {
                    accounts.forEach { acc ->
                        DropdownMenuItem(text = { Text("${acc.name} (${acc.balance})") }, onClick = { sourceAccount = acc; sourceExpanded = false })
                    }
                }
            }

            ExposedDropdownMenuBox(expanded = destExpanded, onExpandedChange = { destExpanded = it }) {
                OutlinedTextField(
                    value = destAccount?.name ?: "To Account",
                    onValueChange = {}, readOnly = true, label = { Text("Destination") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = destExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = destExpanded, onDismissRequest = { destExpanded = false }) {
                    accounts.forEach { acc ->
                        if (acc.id != sourceAccount?.id) {
                            DropdownMenuItem(text = { Text("${acc.name} (${acc.balance})") }, onClick = { destAccount = acc; destExpanded = false })
                        }
                    }
                }
            }

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val transferAmt = amount.toDoubleOrNull() ?: 0.0
                    if (sourceAccount != null && destAccount != null && transferAmt > 0) {
                        val splits = listOf(
                            TransactionSplitDto(sourceAccount!!.id, -transferAmt),
                            TransactionSplitDto(destAccount!!.id, transferAmt)
                        )
                        viewModel.recordTransaction("Transfer", System.currentTimeMillis(), splits, emptyList())
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = sourceAccount != null && destAccount != null && amount.isNotBlank() && state !is Resource.Loading
            ) {
                if (state is Resource.Loading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                else Text("Execute Transfer")
            }
        }
    }
}