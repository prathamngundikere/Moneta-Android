package com.prathamngundikere.moneta.presentation.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prathamngundikere.moneta.core.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    fun connect(url: String, apiKey: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            sessionManager.saveCredentials(url, apiKey)
            onSuccess()
        }
    }
}