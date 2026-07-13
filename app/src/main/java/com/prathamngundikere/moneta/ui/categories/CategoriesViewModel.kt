package com.prathamngundikere.moneta.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prathamngundikere.moneta.data.db.CategoryEntity
import com.prathamngundikere.moneta.data.repository.CategoryRepository
import com.prathamngundikere.moneta.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val repository: CategoryRepository
) : ViewModel() {

    val categories = repository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    init { refreshCategories() }

    fun refreshCategories() {
        viewModelScope.launch {
            _isRefreshing.value = true
            repository.refreshCategories().onFailure {
                _uiState.value = UiState.Error(it.message ?: "Failed to refresh categories")
            }
            _isRefreshing.value = false
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.createCategory(name).fold(
                onSuccess = { _uiState.value = UiState.Success(Unit); refreshCategories() },
                onFailure = { _uiState.value = UiState.Error(it.message ?: "Failed to add category") }
            )
        }
    }
    fun consumeState() { _uiState.value = UiState.Idle }
}