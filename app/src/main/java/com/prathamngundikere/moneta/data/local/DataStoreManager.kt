package com.prathamngundikere.moneta.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    companion object {
        val BASE_URL = stringPreferencesKey("base_url")
        val API_KEY = stringPreferencesKey("api_key")
        val IS_SETUP_COMPLETE = booleanPreferencesKey("is_setup_complete")
    }

    val baseUrlFlow: Flow<String?> = context.dataStore.data.map { it[BASE_URL] }
    val apiKeyFlow: Flow<String?> = context.dataStore.data.map { it[API_KEY] }
    val isSetupCompleteFlow: Flow<Boolean> = context.dataStore.data.map { it[IS_SETUP_COMPLETE] ?: false }

    suspend fun saveConfig(url: String, key: String) {
        context.dataStore.edit { prefs ->
            prefs[BASE_URL] = url
            prefs[API_KEY] = key
        }
    }

    suspend fun markSetupComplete() {
        context.dataStore.edit { it[IS_SETUP_COMPLETE] = true }
    }
}