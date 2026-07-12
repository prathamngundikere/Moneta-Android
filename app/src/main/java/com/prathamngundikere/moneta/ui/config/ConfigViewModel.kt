package com.prathamngundikere.moneta.ui.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prathamngundikere.moneta.data.repository.ConfigRepository
import com.prathamngundikere.moneta.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ConfigViewModel @Inject constructor(
    private val repository: ConfigRepository
) : ViewModel() {

    // Using the global UiState with Unit
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState.asStateFlow()

    fun testConnection(url: String, apiKey: String) {
        if (url.isBlank() || apiKey.isBlank()) {
            _uiState.value = UiState.Error("URL and Password cannot be empty.")
            return
        }

        _uiState.value = UiState.Loading

        viewModelScope.launch {
            val result = repository.testAndSaveConfig(url, apiKey)

            result.fold(
                onSuccess = {
                    // Pass Unit into the Success state
                    _uiState.value = UiState.Success(Unit)
                },
                onFailure = { exception ->
                    _uiState.value = UiState.Error(exception.message ?: "An unknown error occurred.")
                }
            )
        }
    }

    fun consumeError() {
        _uiState.value = UiState.Idle
    }
}