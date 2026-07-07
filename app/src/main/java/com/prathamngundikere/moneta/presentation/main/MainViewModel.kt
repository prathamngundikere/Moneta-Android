package com.prathamngundikere.moneta.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prathamngundikere.moneta.core.Resource
import com.prathamngundikere.moneta.domain.model.*
import com.prathamngundikere.moneta.domain.repository.MonetaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// presentation/main/MainViewModel.kt
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MonetaRepository
) : ViewModel() {

    private val _accounts = MutableStateFlow<Resource<List<Account>>>(Resource.Loading())
    val accounts: StateFlow<Resource<List<Account>>> = _accounts.asStateFlow()

    private val _categories = MutableStateFlow<Resource<List<Category>>>(Resource.Loading())
    val categories: StateFlow<Resource<List<Category>>> = _categories.asStateFlow()

    private val _items = MutableStateFlow<Resource<List<Item>>>(Resource.Loading())
    val items: StateFlow<Resource<List<Item>>> = _items.asStateFlow()

    init {
        fetchAccounts()
        fetchCategories()
        fetchItems()
    }

    private fun fetchAccounts() {
        viewModelScope.launch {
            repository.getAccounts().collect { _accounts.value = it }
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            repository.getCategories().collect { _categories.value = it }
        }
    }

    private fun fetchItems() {
        viewModelScope.launch {
            repository.getItems().collect { _items.value = it }
        }
    }
}