package com.prathamngundikere.moneta.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prathamngundikere.moneta.core.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@HiltViewModel
class SplashViewModel @Inject constructor(
    sessionManager: SessionManager
) : ViewModel() {
    val isLoading = MutableStateFlow(true)
    val startDestination = MutableStateFlow("setup")

    init {
        viewModelScope.launch {
            sessionManager.serverUrl.firstOrNull().let { url ->
                if (url.isNullOrBlank()) {
                    startDestination.value = "setup"
                } else {
                    startDestination.value = "main"
                }
                isLoading.value = false
            }
        }
    }
}