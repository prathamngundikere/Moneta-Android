package com.prathamngundikere.moneta.data.model

data class Account(
    val id: String? = null,
    val name: String,
    val accountType: String, // ASSET, LIABILITY, RECEIVABLE
    val balance: Double,
    val isActive: Boolean = true
)

data class SystemSettings(val id: String?, val currencyCode: String, val currencySymbol: String)

data class TransactionPayloadDTO(
    val merchant: String,
    val transactionDate: String,
    val notes: String,
    val splits: List<SplitDTO>,
    val lineItems: List<LineItemDTO>? = null
)

data class SplitDTO(val accountId: String, val amount: Double)
data class LineItemDTO(val itemId: String, val quantity: Double, val unitPrice: Double)

data class Transaction(
    val id: String, val merchant: String, val transactionDate: String, val totalAmount: Double
)

data class PageTransaction(val content: List<Transaction>)
data class PageAccount(val content: List<Account>)

data class Category(
    val id: String? = null,
    val name: String,
    val parentCategory: Category? = null
)

data class Item(
    val id: String? = null,
    val name: String,
    val description: String? = null,
    val category: Category? = null
)

data class ItemRequestDTO(
    val name: String,
    val description: String? = null,
    val categoryId: String? = null
)

data class BulkAssignCategoryRequestDTO(
    val itemIds: List<String>,
    val categoryId: String
)

data class RecurringTransaction(
    val id: String? = null,
    val name: String,
    val frequency: String, // DAILY, WEEKLY, MONTHLY, YEARLY
    val nextExecutionDate: String,
    val isActive: Boolean = true,
    val payload: TransactionPayloadDTO
)

// Pagination Wrappers
data class PageCategory(val content: List<Category>)
data class PageItem(val content: List<Item>)
data class PageRecurringTransaction(val content: List<RecurringTransaction>)
data class PageItemHistoryResponseDTO(val content: List<ItemHistoryResponseDTO>)
data class ItemHistoryResponseDTO(
    val merchant: String,
    val purchaseDate: String,
    val quantity: Double,
    val unitPrice: Double,
    val totalPaid: Double
)