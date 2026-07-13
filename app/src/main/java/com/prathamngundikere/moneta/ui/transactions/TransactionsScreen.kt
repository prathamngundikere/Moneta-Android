package com.prathamngundikere.moneta.ui.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.prathamngundikere.moneta.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    onNavigateToAdd: () -> Unit,
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    val transactions by viewModel.transactions.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val symbol by viewModel.currencySymbol.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()

    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Transactions", fontWeight = FontWeight.Bold) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(painterResource(R.drawable.ic_add), contentDescription = "Add Transaction")
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshTransactions() },
            state = pullRefreshState,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(transactions, key = { it.id }) { tx ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { expanded = !expanded }
                    ) {
                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Column {
                                    Text(tx.merchant, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = tx.transactionDate.substringBefore("T"),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "$symbol${"%.2f".format(tx.totalAmount)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (tx.totalAmount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (expanded) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                                Text(
                                    text = "Notes: ${tx.notes ?: "No notes provided"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Transaction ID: ${tx.id}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}