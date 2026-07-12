package com.prathamngundikere.moneta.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prathamngundikere.moneta.data.model.dto.AccountInitRequest
import com.prathamngundikere.moneta.data.model.dto.CurrencyDto
import com.prathamngundikere.moneta.data.repository.SetupRepository
import com.prathamngundikere.moneta.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val repository: SetupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _currencies = MutableStateFlow<List<CurrencyDto>>(emptyList())
    val currencies = _currencies.asStateFlow()

    init { fetchCurrencies() }

    private fun fetchCurrencies() {
        viewModelScope.launch {
            repository.fetchCurrencies().onSuccess { _currencies.value = it }
        }
    }

    fun completeSetup(currency: CurrencyDto, drafts: List<AccountInitRequest>) {
        if (drafts.isEmpty()) {
            _uiState.value = UiState.Error("Please add at least one account")
            return
        }

        _uiState.value = UiState.Loading
        viewModelScope.launch {
            repository.completeSetup(currency, drafts).fold(
                onSuccess = { _uiState.value = UiState.Success(Unit) },
                onFailure = { _uiState.value = UiState.Error(it.message ?: "Setup Failed") }
            )
        }
    }

    fun consumeError() { _uiState.value = UiState.Idle }
}