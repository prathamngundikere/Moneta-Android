package com.prathamngundikere.moneta.ui.transactions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prathamngundikere.moneta.data.model.dto.TransactionDetailDto
import com.prathamngundikere.moneta.data.repository.AccountRepository
import com.prathamngundikere.moneta.data.repository.ItemRepository
import com.prathamngundikere.moneta.data.repository.TransactionRepository
import com.prathamngundikere.moneta.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    accountRepository: AccountRepository,
    itemRepository: ItemRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val transactionId: String = checkNotNull(savedStateHandle["transactionId"])

    private val _detail = MutableStateFlow<TransactionDetailDto?>(null)
    val detail = _detail.asStateFlow()

    // Fetch accounts and items to map IDs to their actual names in the UI
    val accounts = accountRepository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val items = itemRepository.getAllItems()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _currencySymbol = MutableStateFlow("")
    val currencySymbol = _currencySymbol.asStateFlow()

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _currencySymbol.value = accountRepository.getSymbol()
            transactionRepository.getTransactionById(transactionId).fold(
                onSuccess = {
                    _detail.value = it
                    _uiState.value = UiState.Success(Unit)
                },
                onFailure = { _uiState.value = UiState.Error(it.message ?: "Failed to load") }
            )
        }
    }
}