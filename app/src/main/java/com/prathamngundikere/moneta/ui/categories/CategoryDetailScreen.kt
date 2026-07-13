package com.prathamngundikere.moneta.ui.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.prathamngundikere.moneta.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToItem: (String) -> Unit,
    viewModel: CategoryDetailViewModel = hiltViewModel()
) {
    val category by viewModel.category.collectAsState()
    val items by viewModel.itemsInCategory.collectAsState()
    val allItems by viewModel.allItems.collectAsState()
    var showAssignDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category?.name ?: "Category Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(painterResource(R.drawable.ic_arrow_back), "Back") }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAssignDialog = true },
                text = { Text("Assign Items") },
                icon = { Icon(painterResource(R.drawable.ic_list), contentDescription = null) }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
            if (items.isEmpty()) {
                item { Text("No items in this category yet.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            items(items, key = { it.id }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    onClick = { onNavigateToItem(item.id) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        if (item.description != null) Text(item.description, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }

    if (showAssignDialog) {
        var selectedItemIds by remember { mutableStateOf(items.map { it.id }.toSet()) }

        AlertDialog(
            onDismissRequest = { showAssignDialog = false },
            title = { Text("Assign Items") },
            text = {
                LazyColumn {
                    items(allItems) { item ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Checkbox(
                                checked = selectedItemIds.contains(item.id),
                                onCheckedChange = { checked ->
                                    selectedItemIds = if (checked) selectedItemIds + item.id else selectedItemIds - item.id
                                }
                            )
                            Text(item.name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.assignItems(selectedItemIds.toList()); showAssignDialog = false }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showAssignDialog = false }) { Text("Cancel") }
            }
        )
    }
}