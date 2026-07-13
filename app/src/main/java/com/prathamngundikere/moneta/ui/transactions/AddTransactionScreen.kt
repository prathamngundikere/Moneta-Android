package com.prathamngundikere.moneta.ui.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.prathamngundikere.moneta.R
import com.prathamngundikere.moneta.data.db.AccountEntity
import com.prathamngundikere.moneta.data.db.ItemEntity
import com.prathamngundikere.moneta.ui.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val accounts by viewModel.accounts.collectAsState()
    val items by viewModel.items.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Expense", "Transfer")

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Success -> {
                onNavigateBack() // Go back automatically on success
                viewModel.consumeState()
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar((uiState as UiState.Error).message)
                viewModel.consumeState()
            }
            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("New Transaction") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (selectedTabIndex == 0) {
                    ExpenseForm(accounts, items, uiState is UiState.Loading) { merchant, amount, accountId, itemId, notes ->
                        viewModel.submitExpense(merchant, amount, accountId, itemId, notes)
                    }
                } else {
                    TransferForm(accounts, uiState is UiState.Loading) { amount, sourceId, destId, notes ->
                        viewModel.submitTransfer(amount, sourceId, destId, notes)
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseForm(
    accounts: List<AccountEntity>,
    items: List<ItemEntity>,
    isLoading: Boolean,
    onSubmit: (String, Double, String, String, String) -> Unit
) {
    var merchant by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedAccount by remember { mutableStateOf<AccountEntity?>(null) }
    var selectedItem by remember { mutableStateOf<ItemEntity?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = merchant,
            onValueChange = { merchant = it },
            label = { Text("Merchant") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = amountStr,
            onValueChange = { amountStr = it },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        DropdownSelector(
            label = "Pay From Account",
            items = accounts,
            selectedItem = selectedAccount,
            itemLabel = { it.name },
            onItemSelected = { selectedAccount = it }
        )

        DropdownSelector(
            label = "Item/Category",
            items = items,
            selectedItem = selectedItem,
            itemLabel = { it.name },
            onItemSelected = { selectedItem = it }
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Button(
            onClick = {
                val amount = amountStr.toDoubleOrNull() ?: 0.0
                if (selectedAccount != null && selectedItem != null) {
                    onSubmit(merchant, amount, selectedAccount!!.id, selectedItem!!.id, notes)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            enabled = !isLoading && merchant.isNotBlank() && amountStr.isNotBlank() && selectedAccount != null && selectedItem != null
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp)) else Text("Save Expense")
        }
    }
}

@Composable
fun TransferForm(
    accounts: List<AccountEntity>,
    isLoading: Boolean,
    onSubmit: (Double, String, String, String) -> Unit
) {
    var amountStr by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var sourceAccount by remember { mutableStateOf<AccountEntity?>(null) }
    var destAccount by remember { mutableStateOf<AccountEntity?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = amountStr,
            onValueChange = { amountStr = it },
            label = { Text("Transfer Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        DropdownSelector(
            label = "From Account",
            items = accounts,
            selectedItem = sourceAccount,
            itemLabel = { it.name },
            onItemSelected = { sourceAccount = it }
        )

        DropdownSelector(
            label = "To Account",
            items = accounts,
            selectedItem = destAccount,
            itemLabel = { it.name },
            onItemSelected = { destAccount = it }
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Button(
            onClick = {
                val amount = amountStr.toDoubleOrNull() ?: 0.0
                if (sourceAccount != null && destAccount != null) {
                    onSubmit(amount, sourceAccount!!.id, destAccount!!.id, notes)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            enabled = !isLoading && amountStr.isNotBlank() && sourceAccount != null && destAccount != null && sourceAccount?.id != destAccount?.id
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp)) else Text("Process Transfer")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownSelector(
    label: String,
    items: List<T>,
    selectedItem: T?,
    itemLabel: (T) -> String,
    onItemSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedItem?.let(itemLabel) ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            if (items.isEmpty()) {
                DropdownMenuItem(text = { Text("No items available") }, onClick = { expanded = false })
            } else {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(itemLabel(item)) },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}