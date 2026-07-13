package com.prathamngundikere.moneta.data.network

import com.prathamngundikere.moneta.data.model.dto.AccountCreateRequest
import com.prathamngundikere.moneta.data.model.dto.AccountDto
import com.prathamngundikere.moneta.data.model.dto.AccountInitRequest
import com.prathamngundikere.moneta.data.model.dto.AccountUpdateRequest
import com.prathamngundikere.moneta.data.model.dto.CurrencyDto
import com.prathamngundikere.moneta.data.model.dto.ItemCreateRequest
import com.prathamngundikere.moneta.data.model.dto.ItemDto
import com.prathamngundikere.moneta.data.model.dto.ItemUpdateRequest
import com.prathamngundikere.moneta.data.model.dto.PageResponse
import com.prathamngundikere.moneta.data.model.dto.PingResponse
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
}