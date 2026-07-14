package com.prathamngundikere.moneta.data.network

import com.prathamngundikere.moneta.data.model.dto.AccountCreateRequest
import com.prathamngundikere.moneta.data.model.dto.AccountDto
import com.prathamngundikere.moneta.data.model.dto.AccountInitRequest
import com.prathamngundikere.moneta.data.model.dto.AccountStatementDto
import com.prathamngundikere.moneta.data.model.dto.AccountUpdateRequest
import com.prathamngundikere.moneta.data.model.dto.CategoryCreateRequest
import com.prathamngundikere.moneta.data.model.dto.CategoryDto
import com.prathamngundikere.moneta.data.model.dto.CurrencyDto
import com.prathamngundikere.moneta.data.model.dto.ItemAssignCategoryRequest
import com.prathamngundikere.moneta.data.model.dto.ItemCreateRequest
import com.prathamngundikere.moneta.data.model.dto.ItemDto
import com.prathamngundikere.moneta.data.model.dto.ItemHistoryDto
import com.prathamngundikere.moneta.data.model.dto.ItemUpdateRequest
import com.prathamngundikere.moneta.data.model.dto.PageResponse
import com.prathamngundikere.moneta.data.model.dto.PingResponse
import com.prathamngundikere.moneta.data.model.dto.TransactionDetailDto
import com.prathamngundikere.moneta.data.model.dto.TransactionDto
import com.prathamngundikere.moneta.data.model.dto.TransactionPayloadDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("/api/ping")
    suspend fun pingServer(): Response<PingResponse>

    @GET("/api/setup/supported-currencies")
    suspend fun getSupportedCurrencies(): Response<List<CurrencyDto>>

    @POST("/api/setup/currency")
    suspend fun setCurrency(
        @Query("code") code: String,
        @Query("symbol") symbol: String
    ): Response<Unit>

    @POST("/api/setup/initialize")
    suspend fun initializeAccounts(@Body accounts: List<AccountInitRequest>): Response<List<AccountDto>>

    @GET("/api/accounts")
    suspend fun getAccounts(): Response<PageResponse<AccountDto>>

    @POST("/api/accounts")
    suspend fun createAccount(@Body request: AccountCreateRequest): Response<AccountDto>

    @PUT("/api/accounts/{id}")
    suspend fun updateAccount(
        @Path("id") id: String,
        @Body request: AccountUpdateRequest
    ): Response<AccountDto>

    @GET("/api/items")
    suspend fun getItems(): Response<PageResponse<ItemDto>>

    @GET("/api/items/{id}")
    suspend fun getItem(@Path("id") id: String): Response<ItemDto>

    @POST("/api/items")
    suspend fun createItem(@Body request: ItemCreateRequest): Response<ItemDto>

    @PUT("/api/items/{id}")
    suspend fun updateItem(
        @Path("id") id: String,
        @Body request: ItemUpdateRequest
    ): Response<ItemDto>

    @GET("/api/categories")
    suspend fun getCategories(): Response<PageResponse<CategoryDto>>

    @POST("/api/categories")
    suspend fun createCategory(@Body request: CategoryCreateRequest): Response<CategoryDto>

    @PUT("/api/items/assign-category")
    suspend fun assignItemsToCategory(@Body request: ItemAssignCategoryRequest): Response<Unit>

    @GET("/api/transactions")
    suspend fun getTransactions(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): Response<PageResponse<TransactionDto>>

    @POST("/api/transactions")
    suspend fun createTransaction(@Body request: TransactionPayloadDto): Response<TransactionDto>

    @GET("/api/accounts/{id}/transactions")
    suspend fun getAccountTransactions(@Path("id") id: String, @Query("page") page: Int = 0): Response<PageResponse<AccountStatementDto>>

    @GET("/api/items/{id}/history")
    suspend fun getItemHistory(@Path("id") id: String, @Query("page") page: Int = 0): Response<PageResponse<ItemHistoryDto>>

    @GET("/api/transactions/{id}")
    suspend fun getTransactionById(@Path("id") id: String): Response<TransactionDetailDto>
}