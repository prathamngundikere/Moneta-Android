package com.prathamngundikere.moneta.data.remote

import com.prathamngundikere.moneta.core.SessionManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import jakarta.inject.*
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

@Singleton
class MonetaApiClient @Inject constructor(
    private val sessionManager: SessionManager
) {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
    }

    suspend fun getBaseUrl(): String = sessionManager.serverUrl.first() ?: ""
    suspend fun getApiKey(): String = sessionManager.apiKey.first() ?: ""

    suspend inline fun <reified T> get(endpoint: String): T {
        return client.get("${getBaseUrl()}$endpoint") {
            header("X-API-KEY", getApiKey())
            contentType(ContentType.Application.Json)
        }.body()
    }

    suspend inline fun <reified T> post(endpoint: String, body: T): io.ktor.client.statement.HttpResponse {
        return client.post("${getBaseUrl()}$endpoint") {
            header("X-API-KEY", getApiKey())
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }
}