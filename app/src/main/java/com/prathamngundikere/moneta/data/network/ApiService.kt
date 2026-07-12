package com.prathamngundikere.moneta.data.network

import com.prathamngundikere.moneta.data.model.dto.AccountDto
import com.prathamngundikere.moneta.data.model.dto.AccountInitRequest
import com.prathamngundikere.moneta.data.model.dto.CurrencyDto
import com.prathamngundikere.moneta.data.model.dto.PingResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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
}