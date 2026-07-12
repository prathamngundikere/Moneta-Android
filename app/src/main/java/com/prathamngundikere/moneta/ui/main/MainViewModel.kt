package com.prathamngundikere.moneta.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prathamngundikere.moneta.data.datastore.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {

    // Controls whether the OS Splash Screen stays visible
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Determines where the NavHost should start
    private val _startDestination = MutableStateFlow("config")
    val startDestination: StateFlow<String> = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                if (settingsManager.hasSavedConfig()) {
                    val isSetupCompleted = settingsManager.isSetupCompleted()
                    _startDestination.value = if (isSetupCompleted) "home" else "setup"
                } else {
                    _startDestination.value = "config"
                }
            } catch (e: Exception) {
                _startDestination.value = "config"
            } finally {
                _isLoading.value = false
            }
        }
    }
}