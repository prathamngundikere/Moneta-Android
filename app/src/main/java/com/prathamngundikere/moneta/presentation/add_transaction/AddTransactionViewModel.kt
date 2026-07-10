package com.prathamngundikere.moneta.presentation.add_transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prathamngundikere.moneta.core.Resource
import com.prathamngundikere.moneta.data.remote.dto.TransactionLineItemDto
import com.prathamngundikere.moneta.data.remote.dto.TransactionPayloadDto
import com.prathamngundikere.moneta.data.remote.dto.TransactionSplitDto
import com.prathamngundikere.moneta.domain.model.*
import com.prathamngundikere.moneta.domain.repository.MonetaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val repository: MonetaRepository
) : ViewModel() {

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts = _accounts.asStateFlow()

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items = _items.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _uiState = MutableStateFlow<Resource<Unit>?>(null)
    val uiState = _uiState.asStateFlow()

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            repository.getAccounts().collect { if (it is Resource.Success) _accounts.value = it.data ?: emptyList() }
        }
        viewModelScope.launch {
            repository.getItems().collect { if (it is Resource.Success) _items.value = it.data ?: emptyList() }
        }
        viewModelScope.launch {
            repository.getCategories().collect { if (it is Resource.Success) _categories.value = it.data ?: emptyList() }
        }
    }

    fun recordTransaction(merchant: String, dateMillis: Long, splits: List<TransactionSplitDto>, lineItems: List<TransactionLineItemDto>) {
        viewModelScope.launch {
            val dateStr = Instant.ofEpochMilli(dateMillis).atZone(ZoneId.systemDefault()).format(
                DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            val payload = TransactionPayloadDto(
                merchant = merchant,
                transactionDate = dateStr,
                splits = splits,
                lineItems = lineItems
            )
            repository.recordTransaction(payload).collect {
                _uiState.value = it
            }
        }
    }

    fun createItemInline(name: String, categoryId: String) {
        viewModelScope.launch {
            repository.createItem(name, null, categoryId).collect {
                if (it is Resource.Success) {
                    repository.getItems().collect { res ->
                        if (res is Resource.Success) _items.value = res.data ?: emptyList()
                    }
                }
            }
        }
    }
}