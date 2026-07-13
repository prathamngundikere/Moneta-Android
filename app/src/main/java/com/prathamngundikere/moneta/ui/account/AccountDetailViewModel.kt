package com.prathamngundikere.moneta.ui.account

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prathamngundikere.moneta.data.db.AccountEntity
import com.prathamngundikere.moneta.data.repository.AccountRepository
import com.prathamngundikere.moneta.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AccountDetailViewModel @Inject constructor(
    private val repository: AccountRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val accountId: String = checkNotNull(savedStateHandle["accountId"])

    @OptIn(ExperimentalCoroutinesApi::class)
    val account: StateFlow<AccountEntity?> = repository.getAccountById(accountId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _currencySymbol = MutableStateFlow("")
    val currencySymbol = _currencySymbol.asStateFlow()

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _currencySymbol.value = repository.getSymbol()
        }
    }

    fun updateAccountName(newName: String) {
        if (newName.isBlank()) return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.updateAccountName(accountId, newName).fold(
                onSuccess = { _uiState.value = UiState.Success(Unit) },
                onFailure = { _uiState.value = UiState.Error(it.message ?: "Failed to rename account") }
            )
        }
    }

    fun consumeState() {
        _uiState.value = UiState.Idle
    }
}