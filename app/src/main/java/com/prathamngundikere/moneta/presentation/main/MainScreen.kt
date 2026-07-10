package com.prathamngundikere.moneta.presentation.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.prathamngundikere.moneta.R
import com.prathamngundikere.moneta.core.Resource
import com.prathamngundikere.moneta.domain.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToAddAccount: () -> Unit,
    onNavigateToAddCategory: () -> Unit,
    onNavigateToAddItem: () -> Unit,
    onNavigateToTransfer: () -> Unit,
    onNavigateToAddTransaction: () -> Unit
) {
    val accountsState by viewModel.accounts.collectAsState()
    var isFabExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    Scaffold(
        floatingActionButton = {
            ExpandableFabMenu(
                expanded = isFabExpanded,
                onToggle = { isFabExpanded = !isFabExpanded },
                onAddAccount = { isFabExpanded = false; onNavigateToAddAccount() },
                onAddCategory = { isFabExpanded = false; onNavigateToAddCategory() },
                onAddItem = { isFabExpanded = false; onNavigateToAddItem() },
                onAddTransaction = { isFabExpanded = false; onNavigateToAddTransaction() },
                onTransfer = { isFabExpanded = false; onNavigateToTransfer() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (accountsState) {
                is Resource.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is Resource.Success -> {
                    LazyRow(
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(accountsState.data ?: emptyList()) { account ->
                            AccountCard(account)
                        }
                    }
                }
                is Resource.Error -> {
                    Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                        Text(accountsState.message ?: "Error", color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No recent transactions found.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AccountCard(account: Account) {
    Card(
        modifier = Modifier.width(160.dp).height(100.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = account.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${account.currency} ${account.balance}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (account.balance < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun TransactionExpandableCard(transaction: TransactionUiModel) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = transaction.merchant, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = transaction.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(
                    text = "${transaction.currency} ${transaction.amount}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
                    transaction.items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "${item.quantity}x ${item.name}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "${transaction.currency} ${item.unitPrice * item.quantity}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableFabMenu(
    expanded: Boolean,
    onToggle: () -> Unit,
    onAddAccount: () -> Unit,
    onAddCategory: () -> Unit,
    onAddItem: () -> Unit,
    onAddTransaction: () -> Unit,
    onTransfer: () -> Unit
) {
    Column(horizontalAlignment = Alignment.End) {
        AnimatedVisibility(visible = expanded) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                FabMenuItem(label = "Add Account", icon = R.drawable.ic_account_balance, onClick = onAddAccount)
                FabMenuItem(label = "Add Category", icon = R.drawable.ic_category, onClick = onAddCategory)
                FabMenuItem(label = "Add Item", icon = R.drawable.ic_inventory, onClick = onAddItem)
                FabMenuItem(label = "Transfer", icon = R.drawable.ic_transfer, onClick = onTransfer)
                FabMenuItem(label = "Add Transaction", icon = R.drawable.ic_add_shopping_cart, onClick = onAddTransaction)
            }
        }

        FloatingActionButton(
            onClick = onToggle,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                painter = painterResource(if (expanded) R.drawable.ic_close else R.drawable.ic_add),
                contentDescription = null
            )
        }
    }
}

@Composable
fun FabMenuItem(label: String, icon: Int, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.padding(end = 12.dp)
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelLarge
            )
        }
        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            Icon(painter = painterResource(id = icon), contentDescription = label)
        }
    }
}