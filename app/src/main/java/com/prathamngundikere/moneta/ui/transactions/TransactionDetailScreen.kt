package com.prathamngundikere.moneta.ui.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
fun TransactionDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: TransactionDetailViewModel = hiltViewModel()
) {
    val detail by viewModel.detail.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val items by viewModel.items.collectAsState()
    val symbol by viewModel.currencySymbol.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (detail == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val tx = detail!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Header Info
                Text(tx.merchant, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(tx.transactionDate.replace("T", " "), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(16.dp))

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Amount", style = MaterialTheme.typography.titleMedium)
                            Text("$symbol${"%.2f".format(tx.totalAmount)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        if (!tx.notes.isNullOrBlank()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Text("Notes:", fontWeight = FontWeight.SemiBold)
                            Text(tx.notes)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Line Items (Products Bought)
                if (tx.lineItems.isNotEmpty()) {
                    Text("Items Bought", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    tx.lineItems.forEach { lineItem ->
                        val itemName = items.find { it.id == lineItem.itemId }?.name ?: "Unknown Item"
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(itemName, fontWeight = FontWeight.SemiBold)
                                    Text("Qty: ${lineItem.quantity}", style = MaterialTheme.typography.bodySmall)
                                }
                                Text("$symbol${"%.2f".format(lineItem.lineTotal)}", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Splits (Which accounts were used)
                Text("Payment / Accounts", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                tx.splits.forEach { split ->
                    val accountName = accounts.find { it.id == split.accountId }?.name ?: "Unknown Account"
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(accountName, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "${if (split.amount > 0) "+" else ""}$symbol${"%.2f".format(split.amount)}",
                                color = if (split.amount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}