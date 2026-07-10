package com.prathamngundikere.moneta.presentation.add_account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prathamngundikere.moneta.core.Resource
import com.prathamngundikere.moneta.domain.model.AccountType
import com.prathamngundikere.moneta.domain.repository.MonetaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AddAccountViewModel @Inject constructor(
    private val repository: MonetaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<Resource<Unit>?>(null)
    val uiState = _uiState.asStateFlow()

    fun createAccount(name: String, type: AccountType, balance: Double, currency: String) {
        viewModelScope.launch {
            repository.createAccount(name, type, balance, currency).collect {
                _uiState.value = it
            }
        }
    }
}