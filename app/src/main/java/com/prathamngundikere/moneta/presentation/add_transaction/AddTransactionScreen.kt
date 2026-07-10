package com.prathamngundikere.moneta.presentation.add_transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.prathamngundikere.moneta.R
import com.prathamngundikere.moneta.core.Resource
import com.prathamngundikere.moneta.data.remote.dto.TransactionLineItemDto
import com.prathamngundikere.moneta.data.remote.dto.TransactionSplitDto
import com.prathamngundikere.moneta.domain.model.Account
import com.prathamngundikere.moneta.domain.model.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: AddTransactionViewModel,
    onNavigateBack: () -> Unit
) {
    val accounts by viewModel.accounts.collectAsState()
    val itemsList by viewModel.items.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val state by viewModel.uiState.collectAsState()

    var merchant by remember { mutableStateOf("") }
    var selectedAccount by remember { mutableStateOf<Account?>(null) }
    var accountExpanded by remember { mutableStateOf(false) }

    val lineItems = remember { mutableStateListOf<TransactionLineItemDto>() }

    var showInlineItemDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        if (state is Resource.Success) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Record Transaction") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(painterResource(R.drawable.ic_close), contentDescription = "Close") }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = merchant,
                    onValueChange = { merchant = it },
                    label = { Text("Merchant") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                ExposedDropdownMenuBox(
                    expanded = accountExpanded,
                    onExpandedChange = { accountExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedAccount?.name ?: "Select Payment Account",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pay From") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = accountExpanded,
                        onDismissRequest = { accountExpanded = false }
                    ) {
                        accounts.forEach { acc ->
                            DropdownMenuItem(
                                text = { Text("${acc.name} (${acc.balance})") },
                                onClick = {
                                    selectedAccount = acc
                                    accountExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                Text("Line Items", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            items(lineItems.size) { index ->
                val lineItem = lineItems[index]
                val itemData = itemsList.find { it.id == lineItem.itemId }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${lineItem.quantity}x ${itemData?.name ?: "Unknown"}")
                    Text("${lineItem.unitPrice * lineItem.quantity}")
                    IconButton(onClick = { lineItems.removeAt(index) }) {
                        Icon(painterResource(R.drawable.ic_close), contentDescription = "Remove")
                    }
                }
            }

            item {
                Button(
                    onClick = { showInlineItemDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                ) {
                    Text("Add Item to Cart")
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                val totalAmount = lineItems.sumOf { it.quantity * it.unitPrice }
                Text("Total: $totalAmount", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                Button(
                    onClick = {
                        if (selectedAccount != null && merchant.isNotBlank() && lineItems.isNotEmpty()) {
                            val splits = listOf(
                                TransactionSplitDto(
                                    selectedAccount!!.id,
                                    -totalAmount
                                )
                            )
                            viewModel.recordTransaction(merchant, System.currentTimeMillis(), splits, lineItems)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedAccount != null && merchant.isNotBlank() && lineItems.isNotEmpty() && state !is Resource.Loading
                ) {
                    if (state is Resource.Loading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    else Text("Submit Transaction")
                }
            }
        }
    }

    if (showInlineItemDialog) {
        var selectedItemId by remember { mutableStateOf<String?>(null) }
        var quantity by remember { mutableStateOf("1") }
        var unitPrice by remember { mutableStateOf("") }
        var isCreatingNew by remember { mutableStateOf(false) }

        var newItemName by remember { mutableStateOf("") }
        var newItemCategoryId by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showInlineItemDialog = false },
            title = { Text("Add Item") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!isCreatingNew) {
                        var itemExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(expanded = itemExpanded, onExpandedChange = { itemExpanded = it }) {
                            OutlinedTextField(
                                value = itemsList.find { it.id == selectedItemId }?.name ?: "Select Item",
                                onValueChange = {}, readOnly = true, modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(expanded = itemExpanded, onDismissRequest = { itemExpanded = false }) {
                                itemsList.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(item.name) },
                                        onClick = { selectedItemId = item.id; itemExpanded = false }
                                    )
                                }
                                DropdownMenuItem(
                                    text = { Text("+ Create New Item", color = MaterialTheme.colorScheme.primary) },
                                    onClick = { isCreatingNew = true; itemExpanded = false }
                                )
                            }
                        }
                    } else {
                        OutlinedTextField(value = newItemName, onValueChange = { newItemName = it }, label = { Text("New Item Name") })
                        var catExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(expanded = catExpanded, onExpandedChange = { catExpanded = it }) {
                            OutlinedTextField(
                                value = categories.find { it.id == newItemCategoryId }?.name ?: "Select Category",
                                onValueChange = {}, readOnly = true, modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat.name) },
                                        onClick = { newItemCategoryId = cat.id; catExpanded = false }
                                    )
                                }
                            }
                        }
                        Button(onClick = {
                            if (newItemName.isNotBlank() && newItemCategoryId != null) {
                                viewModel.createItemInline(newItemName, newItemCategoryId!!)
                                isCreatingNew = false
                            }
                        }) { Text("Save Item") }
                    }

                    if (!isCreatingNew) {
                        OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Quantity") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        OutlinedTextField(value = unitPrice, onValueChange = { unitPrice = it }, label = { Text("Unit Price") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    }
                }
            },
            confirmButton = {
                if (!isCreatingNew) {
                    Button(onClick = {
                        val q = quantity.toDoubleOrNull() ?: 0.0
                        val p = unitPrice.toDoubleOrNull() ?: 0.0
                        if (selectedItemId != null && q > 0 && p > 0) {
                            lineItems.add(TransactionLineItemDto(selectedItemId!!, q, p))
                            showInlineItemDialog = false
                        }
                    }) { Text("Add") }
                }
            },
            dismissButton = {
                TextButton(onClick = { showInlineItemDialog = false }) { Text("Cancel") }
            }
        )
    }
}