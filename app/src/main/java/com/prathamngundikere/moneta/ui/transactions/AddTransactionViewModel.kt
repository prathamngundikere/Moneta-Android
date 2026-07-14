package com.prathamngundikere.moneta.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prathamngundikere.moneta.data.db.AccountEntity
import com.prathamngundikere.moneta.data.db.ItemEntity
import com.prathamngundikere.moneta.data.model.dto.LineItemDto
import com.prathamngundikere.moneta.data.model.dto.SplitDto
import com.prathamngundikere.moneta.data.model.dto.TransactionPayloadDto
import com.prathamngundikere.moneta.data.repository.AccountRepository
import com.prathamngundikere.moneta.data.repository.ItemRepository
import com.prathamngundikere.moneta.data.repository.TransactionRepository
import com.prathamngundikere.moneta.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// Data class to hold the dynamic UI state for each item added
data class LineItemEntry(
    val item: ItemEntity? = null,
    val quantity: String = "1",
    val lineTotal: String = ""
)

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    accountRepository: AccountRepository,
    itemRepository: ItemRepository
) : ViewModel() {

    val accounts: StateFlow<List<AccountEntity>> = accountRepository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val items: StateFlow<List<ItemEntity>> = itemRepository.getAllItems()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun submitExpense(
        merchant: String,
        accountId: String,
        dateMillis: Long?,
        notes: String,
        lineItems: List<LineItemEntry>
    ) {
        // Filter out incomplete items
        val validItems = lineItems.filter {
            it.item != null && it.quantity.toDoubleOrNull() != null && it.lineTotal.toDoubleOrNull() != null
        }

        if (merchant.isBlank() || accountId.isBlank() || validItems.isEmpty()) {
            _uiState.value = UiState.Error("Please enter merchant, account, and at least one valid item.")
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading

            // Calculate Grand Total from the valid items
            val grandTotal = validItems.sumOf { it.lineTotal.toDouble() }

            // Format Date (Default to now if null)
            val transactionDate = dateMillis?.let {
                Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDateTime()
            } ?: LocalDateTime.now()

            // Backend Business Logic: Split amount must be negative for an expense out of an account
            val splits = listOf(SplitDto(accountId = accountId, amount = -grandTotal))
            val lineItemDtos = validItems.map {
                LineItemDto(itemId = it.item!!.id, quantity = it.quantity.toDouble(), lineTotal = it.lineTotal.toDouble())
            }

            val payload = TransactionPayloadDto(
                merchant = merchant,
                transactionDate = transactionDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                notes = notes.ifBlank { null },
                splits = splits,
                lineItems = lineItemDtos
            )

            transactionRepository.createTransaction(payload).fold(
                onSuccess = { _uiState.value = UiState.Success(Unit) },
                onFailure = { _uiState.value = UiState.Error(it.message ?: "Failed to save expense") }
            )
        }
    }

    fun submitTransfer(amount: Double, sourceAccountId: String, destAccountId: String, dateMillis: Long?, notes: String) {
        if (amount <= 0 || sourceAccountId.isBlank() || destAccountId.isBlank()) {
            _uiState.value = UiState.Error("Please fill all required fields correctly.")
            return
        }
        if (sourceAccountId == destAccountId) {
            _uiState.value = UiState.Error("Source and Destination accounts cannot be the same.")
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val transactionDate = dateMillis?.let {
                Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDateTime()
            } ?: LocalDateTime.now()

            // Backend Business Logic: Transfers must sum exactly to 0
            val splits = listOf(
                SplitDto(accountId = sourceAccountId, amount = -amount), // Money leaves
                SplitDto(accountId = destAccountId, amount = amount)     // Money enters
            )

            val payload = TransactionPayloadDto(
                merchant = "Transfer",
                transactionDate = transactionDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                notes = notes.ifBlank { null },
                splits = splits,
                lineItems = emptyList() // Transfers have no line items
            )

            transactionRepository.createTransaction(payload).fold(
                onSuccess = { _uiState.value = UiState.Success(Unit) },
                onFailure = { _uiState.value = UiState.Error(it.message ?: "Failed to save transfer") }
            )
        }
    }

    fun consumeState() {
        _uiState.value = UiState.Idle
    }
}