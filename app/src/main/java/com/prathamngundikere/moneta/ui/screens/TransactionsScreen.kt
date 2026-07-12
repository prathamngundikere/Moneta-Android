package com.prathamngundikere.moneta.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.prathamngundikere.moneta.ui.viewmodels.TransactionsViewModel

@Composable
fun TransactionsScreen(viewModel: TransactionsViewModel = hiltViewModel()) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(viewModel.transactions) { transaction ->
            ListItem(
                headlineContent = { Text(transaction.merchant) },
                supportingContent = { Text(transaction.transactionDate.substringBefore("T")) },
                trailingContent = {
                    Text(
                        text = "$${transaction.totalAmount}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
        }
    }
}