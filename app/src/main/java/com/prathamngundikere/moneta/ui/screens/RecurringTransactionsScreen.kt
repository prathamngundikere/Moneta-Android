package com.prathamngundikere.moneta.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.prathamngundikere.moneta.R
import com.prathamngundikere.moneta.ui.viewmodels.RecurringTransactionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringTransactionsScreen(viewModel: RecurringTransactionsViewModel = hiltViewModel()) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Open Add Template Full-Screen Dialog */ }) {
                Icon(painterResource(R.drawable.ic_add), contentDescription = "Add Template")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
            items(viewModel.templates) { template ->
                ListItem(
                    headlineContent = { Text(template.name) },
                    supportingContent = { Text("Runs: ${template.frequency} | Next: ${template.nextExecutionDate}") },
                    trailingContent = {
                        IconButton(onClick = { template.id?.let { viewModel.deactivateTemplate(it) } }) {
                            Icon(painterResource(R.drawable.ic_delete), contentDescription = "Deactivate", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                )
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            }
        }
    }
}