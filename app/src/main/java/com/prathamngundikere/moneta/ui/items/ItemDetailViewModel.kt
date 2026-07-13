package com.prathamngundikere.moneta.ui.items

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prathamngundikere.moneta.data.db.ItemEntity
import com.prathamngundikere.moneta.data.repository.ItemRepository
import com.prathamngundikere.moneta.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    private val repository: ItemRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val itemId: String = checkNotNull(savedStateHandle["itemId"])

    val item: StateFlow<ItemEntity?> = repository.getItemById(itemId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun updateItem(name: String, description: String) {
        if (name.isBlank() || description.isBlank()) return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.updateItem(itemId, name, description).fold(
                onSuccess = { _uiState.value = UiState.Success(Unit) },
                onFailure = { _uiState.value = UiState.Error(it.message ?: "Failed to update item") }
            )
        }
    }

    fun consumeState() {
        _uiState.value = UiState.Idle
    }
}