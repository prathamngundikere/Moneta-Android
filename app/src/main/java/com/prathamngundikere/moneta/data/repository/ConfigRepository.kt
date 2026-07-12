package com.prathamngundikere.moneta.data.repository

import com.prathamngundikere.moneta.data.datastore.SettingsManager
import com.prathamngundikere.moneta.data.network.RetrofitFactory
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.net.UnknownHostException

@Singleton
class ConfigRepository @Inject constructor(
    private val retrofitFactory: RetrofitFactory,
    private val settingsManager: SettingsManager
) {
    suspend fun testAndSaveConfig(url: String, apiKey: String): Result<Unit> {
        return try {
            val apiService = retrofitFactory.create(
                baseUrl = url,
                apiKey = apiKey
            )

            val response = apiService.pingServer()

            if (response.isSuccessful && response.body()?.status == "UP") {
                settingsManager.saveConfig(url, apiKey)

                val isSaved = settingsManager.verifyDataSaved(url, apiKey)
                if (isSaved) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Verification failed: Data was not saved properly."))
                }
            } else if (response.code() == 401) {
                Result.failure(Exception("Unauthorized: Invalid or missing X-API-KEY header."))
            } else {
                Result.failure(Exception("Server returned error code: ${response.code()}"))
            }
        } catch (e: UnknownHostException) {
            Result.failure(Exception("Network Error: Could not reach the server. Check your URL."))
        } catch (e: Exception) {
            Result.failure(Exception("Error: ${e.localizedMessage}"))
        }
    }
}