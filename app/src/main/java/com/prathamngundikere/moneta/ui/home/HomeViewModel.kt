package com.prathamngundikere.moneta.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prathamngundikere.moneta.data.db.AccountEntity
import com.prathamngundikere.moneta.data.model.enums.AccountType
import com.prathamngundikere.moneta.data.repository.AccountRepository
import com.prathamngundikere.moneta.data.repository.TransactionRepository
import com.prathamngundikere.moneta.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AccountRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    val accounts: StateFlow<List<AccountEntity>> = repository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _currencySymbol = MutableStateFlow("")
    val currencySymbol = _currencySymbol.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    val recentTransactions = transactionRepository.getAllTransactions()
        .map { it.take(10) } // Keep it to recent 10 for dashboard
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            _currencySymbol.value = repository.getSymbol()
        }
        refreshAccounts()
        viewModelScope.launch { transactionRepository.refreshTransactions() }
    }

    fun refreshAccounts() {
        viewModelScope.launch {
            _isRefreshing.value = true
            repository.refreshAccounts().onFailure {
                _uiState.value = UiState.Error(it.message ?: "Failed to refresh accounts")
            }
            _isRefreshing.value = false
        }
    }

    fun addAccount(name: String, type: AccountType, balance: Double) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.createAccount(name, type, balance).fold(
                onSuccess = {
                    _uiState.value = UiState.Success(Unit)
                    refreshAccounts() // Optional: Fetch full list again to be safe
                },
                onFailure = {
                    _uiState.value = UiState.Error(it.message ?: "Failed to add account")
                }
            )
        }
    }

    fun consumeState() {
        _uiState.value = UiState.Idle
    }
}