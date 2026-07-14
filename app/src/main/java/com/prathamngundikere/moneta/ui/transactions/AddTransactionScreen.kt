package com.prathamngundikere.moneta.ui.transactions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.prathamngundikere.moneta.R
import com.prathamngundikere.moneta.data.db.AccountEntity
import com.prathamngundikere.moneta.data.db.ItemEntity
import com.prathamngundikere.moneta.ui.UiState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
                onNavigateBack()
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
                    ExpenseForm(accounts, items, uiState is UiState.Loading) { merchant, accountId, dateMillis, notes, lineItems ->
                        viewModel.submitExpense(merchant, accountId, dateMillis, notes, lineItems)
                    }
                } else {
                    TransferForm(accounts, uiState is UiState.Loading) { amount, sourceId, destId, dateMillis, notes ->
                        viewModel.submitTransfer(amount, sourceId, destId, dateMillis, notes)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseForm(
    accounts: List<AccountEntity>,
    items: List<ItemEntity>,
    isLoading: Boolean,
    onSubmit: (String, String, Long?, String, List<LineItemEntry>) -> Unit
) {
    var merchant by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedAccount by remember { mutableStateOf<AccountEntity?>(null) }

    // Dynamic list of items
    var lineItems by remember { mutableStateOf(listOf(LineItemEntry())) }

    // Date Picker State
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    val selectedDate = datePickerState.selectedDateMillis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
    } ?: LocalDate.now()

    // Calculate Grand Total automatically
    val grandTotal = lineItems.sumOf { it.lineTotal.toDoubleOrNull() ?: 0.0 }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { TextButton(onClick = { showDatePicker = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        OutlinedTextField(
            value = selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            onValueChange = {},
            readOnly = true,
            label = { Text("Date") },
            modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
            enabled = false, // Use clickable instead of standard focus
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        OutlinedTextField(
            value = merchant,
            onValueChange = { merchant = it },
            label = { Text("Merchant") },
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

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Text("Items", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        // Dynamic Line Items
        lineItems.forEachIndexed { index, entry ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            DropdownSelector(
                                label = "Select Item",
                                items = items,
                                selectedItem = entry.item,
                                itemLabel = { it.name },
                                onItemSelected = { newItem ->
                                    val newList = lineItems.toMutableList()
                                    newList[index] = entry.copy(item = newItem)
                                    lineItems = newList
                                }
                            )
                        }
                        // Delete Button
                        if (lineItems.size > 1) {
                            IconButton(onClick = {
                                val newList = lineItems.toMutableList()
                                newList.removeAt(index)
                                lineItems = newList
                            }) {
                                Icon(painterResource(R.drawable.ic_delete), contentDescription = "Remove Item", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = entry.quantity,
                            onValueChange = { newQty ->
                                val newList = lineItems.toMutableList()
                                newList[index] = entry.copy(quantity = newQty)
                                lineItems = newList
                            },
                            label = { Text(if (entry.item != null) "Qty (${entry.item.unit})" else "Quantity") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = entry.lineTotal,
                            onValueChange = { newTotal ->
                                val newList = lineItems.toMutableList()
                                newList[index] = entry.copy(lineTotal = newTotal)
                                lineItems = newList
                            },
                            label = { Text("Total Paid") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1.5f),
                            singleLine = true
                        )
                    }
                }
            }
        }

        TextButton(
            onClick = { lineItems = lineItems + LineItemEntry() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("+ Add Another Item")
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        // Grand Total Display
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Grand Total:", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                "%.2f".format(grandTotal),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Button(
            onClick = {
                if (selectedAccount != null) {
                    onSubmit(merchant, selectedAccount!!.id, datePickerState.selectedDateMillis, notes, lineItems)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 32.dp),
            enabled = !isLoading && merchant.isNotBlank() && selectedAccount != null && grandTotal > 0
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp)) else Text("Save Transaction")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferForm(
    accounts: List<AccountEntity>,
    isLoading: Boolean,
    onSubmit: (Double, String, String, Long?, String) -> Unit
) {
    var amountStr by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var sourceAccount by remember { mutableStateOf<AccountEntity?>(null) }
    var destAccount by remember { mutableStateOf<AccountEntity?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    val selectedDate = datePickerState.selectedDateMillis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
    } ?: LocalDate.now()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { TextButton(onClick = { showDatePicker = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        OutlinedTextField(
            value = selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            onValueChange = {},
            readOnly = true,
            label = { Text("Date") },
            modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

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
                    onSubmit(amount, sourceAccount!!.id, destAccount!!.id, datePickerState.selectedDateMillis, notes)
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