package com.prathamngundikere.moneta.ui.categories

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prathamngundikere.moneta.data.repository.CategoryRepository
import com.prathamngundikere.moneta.data.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class CategoryDetailViewModel @Inject constructor(
    private val categoryRepo: CategoryRepository,
    private val itemRepo: ItemRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val categoryId: String = checkNotNull(savedStateHandle["categoryId"])

    val category = categoryRepo.getCategoryById(categoryId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val itemsInCategory = itemRepo.getItemsByCategory(categoryId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Provides all items to allow user to assign them to this category
    val allItems = itemRepo.getAllItems()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun assignItems(itemIds: List<String>) {
        viewModelScope.launch {
            itemRepo.assignItemsToCategory(itemIds, categoryId)
        }
    }
}