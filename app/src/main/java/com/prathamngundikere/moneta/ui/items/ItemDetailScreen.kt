package com.prathamngundikere.moneta.ui.items

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.prathamngundikere.moneta.R
import com.prathamngundikere.moneta.ui.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: ItemDetailViewModel = hiltViewModel()
) {
    val item by viewModel.item.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val history by viewModel.history.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Error -> {
                snackbarHostState.showSnackbar((uiState as UiState.Error).message)
                viewModel.consumeState()
            }
            is UiState.Success -> {
                snackbarHostState.showSnackbar("Item updated successfully")
                viewModel.consumeState()
            }
            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Item Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = "Edit Item"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (item == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
            ) {
                Text(
                    text = item!!.name,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item!!.description ?: "N/A",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Created: ${item!!.createdAt}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text("Purchase History", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 24.dp))

                history.forEach { hist ->
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 6.dp)) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(hist.merchant, fontWeight = FontWeight.Bold)
                                Text(hist.purchaseDate.substringBefore("T"), style = MaterialTheme.typography.bodySmall)
                                Text("Qty: ${hist.quantity} @ ${hist.unitPrice}", style = MaterialTheme.typography.labelSmall)
                            }
                            Text("${hist.totalPaid}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog && item != null) {
        AddItemDialog( // We can reuse AddItemDialog layout for editing by passing default values
            initialName = item!!.name,
            initialDescription = item!!.description ?: "",
            initialUnit = item!!.unit,
            onDismiss = { showEditDialog = false },
            onSave = { name, desc, unit ->
                viewModel.updateItem(name, desc, unit)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun AddItemDialog(
    initialName: String = "",
    initialDescription: String = "",
    initialUnit: String = "UNIT",
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var draftName by remember { mutableStateOf(initialName) }
    var draftDescription by remember { mutableStateOf(initialDescription) }
    var draftUnit by remember { mutableStateOf(initialUnit) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if(initialName.isEmpty()) "Add Item" else "Edit Item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = draftName,
                    onValueChange = { draftName = it },
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = draftUnit,
                    onValueChange = { draftUnit = it },
                    label = { Text("Unit (e.g. KG, L, UNIT)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = draftDescription,
                    onValueChange = { draftDescription = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(draftName, draftDescription, draftUnit) },
                enabled = draftName.isNotBlank() && draftDescription.isNotBlank()
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}