package com.prathamngundikere.moneta.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prathamngundikere.moneta.data.local.DataStoreManager
import com.prathamngundikere.moneta.data.model.Account
import com.prathamngundikere.moneta.data.model.Category
import com.prathamngundikere.moneta.data.model.Item
import com.prathamngundikere.moneta.data.model.ItemRequestDTO
import com.prathamngundikere.moneta.data.model.RecurringTransaction
import com.prathamngundikere.moneta.data.model.Transaction
import com.prathamngundikere.moneta.data.model.TransactionPayloadDTO
import com.prathamngundikere.moneta.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ConfigViewModel @Inject constructor(private val dataStore: DataStoreManager) : ViewModel() {
    fun saveConfig(url: String, key: String) {
        viewModelScope.launch { dataStore.saveConfig(url, key) }
    }
}

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val apiService: ApiService,
    private val dataStore: DataStoreManager
) : ViewModel() {
    var currencies by mutableStateOf<List<Map<String, String>>>(emptyList())
    var accountsToCreate by mutableStateOf<List<Account>>(emptyList())
    var setupComplete by mutableStateOf(false)

    // New states for UI feedback
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        fetchCurrencies()
    }

    private fun fetchCurrencies() {
        viewModelScope.launch {
            try { currencies = apiService.getSupportedCurrencies() } catch (e: Exception) { /* Silent fail for now */ }
        }
    }

    fun addAccountDraft(name: String, type: String, balance: Double) {
        accountsToCreate = accountsToCreate + Account(name = name, accountType = type, balance = balance)
    }

    fun finalizeSetup(selectedCurrencyCode: String, selectedCurrencySymbol: String) {
        // Basic validation before hitting the network
        if (selectedCurrencyCode.isBlank()) {
            errorMessage = "Please enter a valid currency code."
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                // 1. Set Currency
                apiService.setSystemCurrency(selectedCurrencyCode, selectedCurrencySymbol)

                // 2. Initialize Accounts (if any)
                if (accountsToCreate.isNotEmpty()) {
                    apiService.initializeAccounts(accountsToCreate)
                }

                // 3. Mark setup as complete locally
                dataStore.markSetupComplete()
                setupComplete = true

            } catch (e: Exception) {
                if (e is retrofit2.HttpException) {
                    val errorBody = e.response()?.errorBody()?.string() ?: ""

                    // Check if the server is telling us it's already set up
                    if (e.code() == 400 && errorBody.contains("already set", ignoreCase = true)) {
                        // The backend is already configured. Sync our local state and proceed!
                        viewModelScope.launch {
                            dataStore.markSetupComplete()
                            setupComplete = true
                        }
                        return@launch
                    }

                    errorMessage = "Server error 400: $errorBody"
                    e.printStackTrace()
                } else {
                    errorMessage = e.message ?: "An unexpected network error occurred."
                    e.printStackTrace()
                }
            } finally {
                isLoading = false
            }
        }
    }
}

@HiltViewModel
class MainViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {
    var isServerOnline by mutableStateOf(false)

    init {
        startPingRoutine()
    }

    private fun startPingRoutine() {
        viewModelScope.launch {
            while (true) {
                try {
                    apiService.ping()
                    isServerOnline = true
                } catch (e: Exception) {
                    isServerOnline = false
                }
                delay(5000.milliseconds) // Poll every 5 seconds
            }
        }
    }

    fun nukeSystem(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                apiService.nukeSystem()
                onSuccess()
            } catch (e: Exception) { /* Handle error */ }
        }
    }
}

@HiltViewModel
class CategoriesViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {
    var categories by mutableStateOf<List<Category>>(emptyList())
        private set

    init { fetchCategories() }

    private fun fetchCategories() {
        viewModelScope.launch {
            try { categories = apiService.getAllCategories().content }
            catch (e: Exception) { /* Handle error */ }
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            try {
                apiService.createCategory(Category(name = name))
                fetchCategories() // Refresh list
            } catch (e: Exception) { /* Handle error */ }
        }
    }
}

@HiltViewModel
class ItemsViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {
    var items by mutableStateOf<List<Item>>(emptyList())
        private set

    init { fetchItems() }

    private fun fetchItems() {
        viewModelScope.launch {
            try { items = apiService.getAllItems().content }
            catch (e: Exception) { /* Handle error */ }
        }
    }

    fun addItem(name: String, categoryId: String?) {
        viewModelScope.launch {
            try {
                apiService.createItem(ItemRequestDTO(name = name, categoryId = categoryId))
                fetchItems() // Refresh list
            } catch (e: Exception) { /* Handle error */ }
        }
    }
}

@HiltViewModel
class RecurringTransactionsViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    var templates by mutableStateOf<List<RecurringTransaction>>(emptyList())
        private set

    init { fetchTemplates() }

    private fun fetchTemplates() {
        viewModelScope.launch {
            try { templates = apiService.getAllTemplates().content }
            catch (e: Exception) { /* Handle error */ }
        }
    }

    fun addTemplate(name: String, frequency: String, nextDate: String, payload: TransactionPayloadDTO) {
        viewModelScope.launch {
            try {
                apiService.createTemplate(
                    RecurringTransaction(
                        name = name,
                        frequency = frequency,
                        nextExecutionDate = nextDate,
                        payload = payload
                    )
                )
                fetchTemplates()
            } catch (e: Exception) { /* Handle error */ }
        }
    }

    fun deactivateTemplate(id: String) {
        viewModelScope.launch {
            try {
                apiService.deactivateTemplate(id)
                fetchTemplates()
            } catch (e: Exception) { /* Handle error */ }
        }
    }
}

@HiltViewModel
class AccountsViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {
    var accounts by mutableStateOf<List<Account>>(emptyList())
        private set

    init { fetchAccounts() }

    fun fetchAccounts() {
        viewModelScope.launch {
            try { accounts = apiService.getAllAccounts().content }
            catch (e: Exception) { /* Handle error */ }
        }
    }

    fun deleteAccount(id: String) {
        viewModelScope.launch {
            try {
                apiService.deleteAccount(id)
                fetchAccounts()
            } catch (e: Exception) { /* Handle error */ }
        }
    }
}

@HiltViewModel
class TransactionsViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {
    var transactions by mutableStateOf<List<Transaction>>(emptyList())
        private set

    init { fetchTransactions() }

    fun fetchTransactions() {
        viewModelScope.launch {
            try { transactions = apiService.getAllTransactions().content }
            catch (e: Exception) { /* Handle error */ }
        }
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            try {
                apiService.deleteTransaction(id)
                fetchTransactions()
            } catch (e: Exception) { /* Handle error */ }
        }
    }
}

@HiltViewModel
class RootViewModel @Inject constructor(
    dataStoreManager: DataStoreManager
) : ViewModel() {
    val baseUrlFlow = dataStoreManager.baseUrlFlow
    val isSetupCompleteFlow = dataStoreManager.isSetupCompleteFlow
}