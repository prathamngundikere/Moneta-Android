package com.prathamngundikere.moneta.data.remote

import com.prathamngundikere.moneta.data.local.DataStoreManager
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

class DynamicUrlInterceptor @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        // Blocking fetch for current preferences (Network calls are already on background thread)
        val baseUrlString = runBlocking { dataStoreManager.baseUrlFlow.first() }
        val apiKey = runBlocking { dataStoreManager.apiKeyFlow.first() }

        val newBuilder = request.newBuilder()

        if (!apiKey.isNullOrEmpty()) {
            newBuilder.addHeader("X-API-KEY", apiKey)
        }

        if (!baseUrlString.isNullOrEmpty()) {
            try {
                val newBaseUrl = baseUrlString.toHttpUrlOrNull()
                if (newBaseUrl != null) {
                    val newUrl = request.url.newBuilder()
                        .scheme(newBaseUrl.scheme)
                        .host(newBaseUrl.host)
                        .port(newBaseUrl.port)
                        .build()
                    request = newBuilder.url(newUrl).build()
                }
            } catch (e: Exception) {
                // Fallback to original request if parsing fails
            }
        } else {
            request = newBuilder.build()
        }

        return chain.proceed(request)
    }
}