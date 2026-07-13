package com.prathamngundikere.moneta.ui.account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.prathamngundikere.moneta.R
import com.prathamngundikere.moneta.ui.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: AccountDetailViewModel = hiltViewModel()
) {
    val account by viewModel.account.collectAsState()
    val symbol by viewModel.currencySymbol.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    var showEditNameDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Error -> {
                snackbarHostState.showSnackbar((uiState as UiState.Error).message)
                viewModel.consumeState()
            }
            is UiState.Success -> {
                snackbarHostState.showSnackbar("Account updated successfully")
                viewModel.consumeState()
            }
            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Account Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showEditNameDialog = true }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = "Edit Account Name"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (account == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = "Current Balance",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$symbol${"%.2f".format(account!!.balance)}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailRow(label = "Account Name", value = account!!.name)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        DetailRow(label = "Account Type", value = account!!.accountType)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        DetailRow(label = "Status", value = if (account!!.isActive) "Active" else "Inactive")
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text("Transaction History", style = MaterialTheme.typography.titleLarge, modifier = Modifier.align(Alignment.Start).padding(horizontal = 24.dp))
                Spacer(modifier = Modifier.height(8.dp))

                transactions.forEach { tx ->
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 6.dp)) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(tx.merchant, fontWeight = FontWeight.Bold)
                                Text(tx.transactionDate.substringBefore("T"), style = MaterialTheme.typography.bodySmall)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "${if(tx.amountMoved > 0) "+" else ""}$symbol${"%.2f".format(tx.amountMoved)}",
                                    color = if (tx.amountMoved > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("Bal: $symbol${"%.2f".format(tx.currentAccountBalance)}", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditNameDialog && account != null) {
        EditAccountNameDialog(
            currentName = account!!.name,
            onDismiss = { showEditNameDialog = false },
            onSave = { newName ->
                viewModel.updateAccountName(newName)
                showEditNameDialog = false
            }
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAccountNameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var newName by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename Account") },
        text = {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Account Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { if (newName.isNotBlank()) onSave(newName) }
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(newName) },
                enabled = newName.isNotBlank() && newName != currentName
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}