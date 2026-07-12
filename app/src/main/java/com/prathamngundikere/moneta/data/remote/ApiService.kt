package com.prathamngundikere.moneta.data.remote

import com.prathamngundikere.moneta.data.model.Account
import com.prathamngundikere.moneta.data.model.BulkAssignCategoryRequestDTO
import com.prathamngundikere.moneta.data.model.Category
import com.prathamngundikere.moneta.data.model.Item
import com.prathamngundikere.moneta.data.model.ItemRequestDTO
import com.prathamngundikere.moneta.data.model.PageAccount
import com.prathamngundikere.moneta.data.model.PageCategory
import com.prathamngundikere.moneta.data.model.PageItem
import com.prathamngundikere.moneta.data.model.PageItemHistoryResponseDTO
import com.prathamngundikere.moneta.data.model.PageRecurringTransaction
import com.prathamngundikere.moneta.data.model.PageTransaction
import com.prathamngundikere.moneta.data.model.RecurringTransaction
import com.prathamngundikere.moneta.data.model.SystemSettings
import com.prathamngundikere.moneta.data.model.Transaction
import com.prathamngundikere.moneta.data.model.TransactionPayloadDTO
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Setup & Config
    @GET("/api/setup/supported-currencies")
    suspend fun getSupportedCurrencies(): List<Map<String, String>>

    @POST("/api/setup/currency")
    suspend fun setSystemCurrency(@Query("code") code: String, @Query("symbol") symbol: String): SystemSettings

    @POST("/api/setup/initialize")
    suspend fun initializeAccounts(@Body accounts: List<Account>): List<Account>

    @DELETE("/api/setup/nuke")
    suspend fun nukeSystem(): String

    // Health
    @GET("/api/ping")
    suspend fun ping(): Any

    // Accounts
    @GET("/api/accounts")
    suspend fun getAllAccounts(@Query("page") page: Int = 0, @Query("size") size: Int = 20): PageAccount

    @POST("/api/accounts")
    suspend fun createAccount(@Body account: Account): Account

    // Transactions
    @GET("/api/transactions")
    suspend fun getAllTransactions(@Query("page") page: Int = 0, @Query("size") size: Int = 20): PageTransaction

    @POST("/api/transactions")
    suspend fun recordTransaction(@Body payload: TransactionPayloadDTO): Transaction

    // --- Account Management ---
    @PUT("/api/accounts/{id}")
    suspend fun updateAccount(@Path("id") id: String, @Body account: Account): Account

    @DELETE("/api/accounts/{id}")
    suspend fun deleteAccount(@Path("id") id: String)

    @GET("/api/accounts/{id}/transactions")
    suspend fun getAccountHistory(@Path("id") id: String, @Query("page") page: Int = 0): Any // Using Any for brevity on PageAccountStatementResponseDTO

    // --- Transaction Management ---
    @GET("/api/transactions/{id}")
    suspend fun getTransactionById(@Path("id") id: String): Transaction

    @PUT("/api/transactions/{id}")
    suspend fun updateTransaction(@Path("id") id: String, @Body payload: TransactionPayloadDTO): Transaction

    @DELETE("/api/transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: String)

    // --- Categories ---
    @GET("/api/categories")
    suspend fun getAllCategories(@Query("page") page: Int = 0): PageCategory

    @POST("/api/categories")
    suspend fun createCategory(@Body category: Category): Category

    @GET("/api/categories/{id}/transactions")
    suspend fun getCategoryHistory(@Path("id") id: String, @Query("page") page: Int = 0): PageItemHistoryResponseDTO

    // --- Items ---
    @GET("/api/items")
    suspend fun getAllItems(@Query("page") page: Int = 0): PageItem

    @POST("/api/items")
    suspend fun createItem(@Body request: ItemRequestDTO): Item

    @GET("/api/items/{id}")
    suspend fun getItemById(@Path("id") id: String): Item

    @PUT("/api/items/{id}")
    suspend fun updateItem(@Path("id") id: String, @Body item: Item): Item

    @DELETE("/api/items/{id}")
    suspend fun deleteItem(@Path("id") id: String)

    @PUT("/api/items/assign-category")
    suspend fun assignCategoryToItems(@Body request: BulkAssignCategoryRequestDTO)

    @GET("/api/items/{id}/history")
    suspend fun getItemHistory(@Path("id") id: String, @Query("page") page: Int = 0): PageItemHistoryResponseDTO

    // --- Recurring Transactions ---
    @GET("/api/recurring-transactions")
    suspend fun getAllTemplates(@Query("page") page: Int = 0): PageRecurringTransaction

    @POST("/api/recurring-transactions")
    suspend fun createTemplate(@Body template: RecurringTransaction): RecurringTransaction

    @DELETE("/api/recurring-transactions/{id}")
    suspend fun deactivateTemplate(@Path("id") id: String)
}