package com.prathamngundikere.moneta.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SessionManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    companion object {
        val SERVER_URL = stringPreferencesKey("server_url")
        val API_KEY = stringPreferencesKey("api_key")
    }

    val serverUrl: Flow<String?> = context.dataStore.data.map { it[SERVER_URL] }
    val apiKey: Flow<String?> = context.dataStore.data.map { it[API_KEY] }

    suspend fun saveCredentials(url: String, key: String) {
        context.dataStore.edit { prefs ->
            prefs[SERVER_URL] = url
            prefs[API_KEY] = key
        }
    }
}