package com.prathamngundikere.moneta.data.network

import jakarta.inject.Inject
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitFactory @Inject constructor() {

    fun create(baseUrl: String, apiKey: String): ApiService {
        val safeUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"

        // The interceptor intercepts every request and adds the headers
        val headerInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()

            val newRequest = originalRequest.newBuilder()
                .header("Content-Type", "application/json")
                .header("X-API-KEY", apiKey)
                .build()

            chain.proceed(newRequest)
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(safeUrl)
            .client(okHttpClient) // Attach the client with the interceptor
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}