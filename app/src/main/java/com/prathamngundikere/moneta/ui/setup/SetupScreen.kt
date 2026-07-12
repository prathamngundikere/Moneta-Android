package com.prathamngundikere.moneta.ui.setup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.prathamngundikere.moneta.R
import com.prathamngundikere.moneta.data.model.dto.AccountInitRequest
import com.prathamngundikere.moneta.data.model.dto.CurrencyDto
import com.prathamngundikere.moneta.data.model.enums.AccountType
import com.prathamngundikere.moneta.ui.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    onComplete: () -> Unit,
    viewModel: SetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currencies by viewModel.currencies.collectAsState()

    var selectedCurrency by remember { mutableStateOf<CurrencyDto?>(null) }
    var accountDrafts by remember { mutableStateOf(listOf<AccountInitRequest>()) }
    var showAddAccountDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) onComplete()
        if (uiState is UiState.Error) {
            snackbarHostState.showSnackbar((uiState as UiState.Error).message)
            viewModel.consumeError()
        }
    }

    LaunchedEffect(currencies) {
        if (selectedCurrency == null && currencies.isNotEmpty()) selectedCurrency = currencies.first()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TopAppBar(title = { Text("Initial Setup") }) },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .imePadding()
            ) {
                Button(
                    onClick = { selectedCurrency?.let { viewModel.completeSetup(it, accountDrafts) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = uiState !is UiState.Loading && selectedCurrency != null && accountDrafts.isNotEmpty(),
                    shape = MaterialTheme.shapes.large
                ) {
                    if (uiState is UiState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                    } else {
                        Text("Complete Setup", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddAccountDialog = true },
                icon = {
                    Icon(painterResource(R.drawable.ic_add), contentDescription = "Add Account")
                },
                text = { Text("Add Account") }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Currency Selector
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp, top = 8.dp)
            ) {
                OutlinedTextField(
                    value = selectedCurrency?.let { "${it.code} (${it.symbol})" } ?: "Loading...",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Base Currency") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    currencies.forEach { currency ->
                        DropdownMenuItem(
                            text = { Text("${currency.code} (${currency.symbol})") },
                            onClick = { selectedCurrency = currency; expanded = false }
                        )
                    }
                }
            }

            Text(
                text = "Your Accounts",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (accountDrafts.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No accounts added yet.\nTap 'Add Account' to begin.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    items(accountDrafts) { draft ->
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(draft.name, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        text = draft.accountType,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                Text(
                                    text = "${selectedCurrency?.symbol ?: ""}${draft.balance}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddAccountDialog) {
        AddAccountDialog(
            onDismiss = { showAddAccountDialog = false },
            onAddAccount = { name, type, balance ->
                accountDrafts = accountDrafts + AccountInitRequest(name, type.name, balance)
                showAddAccountDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountDialog(
    onDismiss: () -> Unit,
    onAddAccount: (String, AccountType, Double) -> Unit
) {
    var draftName by remember { mutableStateOf("") }
    var draftType by remember { mutableStateOf(AccountType.ASSET) }
    var draftBalance by remember { mutableStateOf("") }
    var typeExpanded by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val balanceFocusRequester = remember { FocusRequester() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Account") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = draftName,
                    onValueChange = { draftName = it },
                    label = { Text("Account Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )

                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = draftType.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Account Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(typeExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                        AccountType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name) },
                                onClick = {
                                    draftType = type
                                    typeExpanded = false
                                    balanceFocusRequester.requestFocus()
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = draftBalance,
                    onValueChange = { draftBalance = it },
                    label = { Text("Starting Balance") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(balanceFocusRequester),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val bal = draftBalance.toDoubleOrNull() ?: 0.0
                    if (draftName.isNotBlank()) {
                        onAddAccount(draftName, draftType, bal)
                    }
                },
                enabled = draftName.isNotBlank() && draftBalance.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}