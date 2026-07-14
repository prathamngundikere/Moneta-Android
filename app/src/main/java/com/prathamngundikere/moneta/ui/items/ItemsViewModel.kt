package com.prathamngundikere.moneta.ui.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prathamngundikere.moneta.data.db.ItemEntity
import com.prathamngundikere.moneta.data.repository.ItemRepository
import com.prathamngundikere.moneta.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ItemsViewModel @Inject constructor(
    private val repository: ItemRepository
) : ViewModel() {

    val items: StateFlow<List<ItemEntity>> = repository.getAllItems()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    init {
        refreshItems()
    }

    fun refreshItems() {
        viewModelScope.launch {
            _isRefreshing.value = true
            repository.refreshItems().onFailure {
                _uiState.value = UiState.Error(it.message ?: "Failed to refresh items")
            }
            _isRefreshing.value = false
        }
    }

    fun addItem(name: String, description: String, unit: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.createItem(name, description, unit).fold(
                onSuccess = {
                    _uiState.value = UiState.Success(Unit)
                    refreshItems()
                },
                onFailure = {
                    _uiState.value = UiState.Error(it.message ?: "Failed to add item")
                }
            )
        }
    }

    fun consumeState() {
        _uiState.value = UiState.Idle
    }
}