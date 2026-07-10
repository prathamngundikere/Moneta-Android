package com.prathamngundikere.moneta.presentation.add_item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prathamngundikere.moneta.core.Resource
import com.prathamngundikere.moneta.domain.model.Category
import com.prathamngundikere.moneta.domain.repository.MonetaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AddItemViewModel @Inject constructor(
    private val repository: MonetaRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _uiState = MutableStateFlow<Resource<Unit>?>(null)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getCategories().collect { result ->
                if (result is Resource.Success) {
                    _categories.value = result.data ?: emptyList()
                }
            }
        }
    }

    fun createItem(name: String, description: String, categoryId: String) {
        viewModelScope.launch {
            repository.createItem(name, description.takeIf { it.isNotBlank() }, categoryId).collect {
                _uiState.value = it
            }
        }
    }
}