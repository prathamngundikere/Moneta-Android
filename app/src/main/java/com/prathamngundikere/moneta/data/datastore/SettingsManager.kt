package com.prathamngundikere.moneta.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.first

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

@Singleton
class SettingsManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    companion object {
        val URL_KEY = stringPreferencesKey("server_url")
        val API_KEY = stringPreferencesKey("server_api_key")
        val SETUP_COMPLETED_KEY = booleanPreferencesKey("setup_completed")
        val CURRENCY_CODE_KEY = stringPreferencesKey("currency_code")
        val CURRENCY_SYMBOL_KEY = stringPreferencesKey("currency_symbol")
    }

    suspend fun saveConfig(url: String, apiKey: String) {
        context.dataStore.edit { prefs ->
            prefs[URL_KEY] = url
            prefs[API_KEY] = apiKey
        }
    }

    suspend fun getCredentials(): Pair<String, String>? {
        val prefs = context.dataStore.data.first()
        val url = prefs[URL_KEY]
        val key = prefs[API_KEY]
        return if (!url.isNullOrBlank() && !key.isNullOrBlank()) Pair(url, key) else null
    }

    suspend fun hasSavedConfig(): Boolean = getCredentials() != null

    suspend fun verifyDataSaved(expectedUrl: String, expectedKey: String): Boolean {
        val prefs = context.dataStore.data.first()
        return prefs[URL_KEY] == expectedUrl && prefs[API_KEY] == expectedKey
    }

    suspend fun completeSetup(currencyCode: String, currencySymbol: String) {
        context.dataStore.edit { prefs ->
            prefs[CURRENCY_CODE_KEY] = currencyCode
            prefs[CURRENCY_SYMBOL_KEY] = currencySymbol
            prefs[SETUP_COMPLETED_KEY] = true
        }
    }

    suspend fun isSetupCompleted(): Boolean {
        val prefs = context.dataStore.data.first()
        return prefs[SETUP_COMPLETED_KEY] ?: false
    }

    suspend fun getCurrencySymbol(): String {
        return context.dataStore.data.first()[CURRENCY_SYMBOL_KEY] ?: ""
    }
}