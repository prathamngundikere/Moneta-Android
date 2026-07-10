package com.prathamngundikere.moneta.presentation.add_category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prathamngundikere.moneta.core.Resource
import com.prathamngundikere.moneta.domain.repository.MonetaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AddCategoryViewModel @Inject constructor(
    private val repository: MonetaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<Resource<Unit>?>(null)
    val uiState = _uiState.asStateFlow()

    fun createCategory(name: String) {
        viewModelScope.launch {
            repository.createCategory(name).collect {
                _uiState.value = it
            }
        }
    }
}