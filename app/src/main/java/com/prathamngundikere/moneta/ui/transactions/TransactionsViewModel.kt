package com.prathamngundikere.moneta.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prathamngundikere.moneta.data.db.TransactionEntity
import com.prathamngundikere.moneta.data.repository.AccountRepository
import com.prathamngundikere.moneta.data.repository.TransactionRepository
import com.prathamngundikere.moneta.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    val transactions = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _currencySymbol = MutableStateFlow("")
    val currencySymbol = _currencySymbol.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        viewModelScope.launch { _currencySymbol.value = accountRepository.getSymbol() }
        refreshTransactions()
    }

    fun refreshTransactions() {
        viewModelScope.launch {
            _isRefreshing.value = true
            repository.refreshTransactions()
            _isRefreshing.value = false
        }
    }
}