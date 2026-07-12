package com.prathamngundikere.moneta.data.repository

import com.prathamngundikere.moneta.data.datastore.SettingsManager
import com.prathamngundikere.moneta.data.db.AccountDao
import com.prathamngundikere.moneta.data.db.AccountEntity
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class AccountRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val settingsManager: SettingsManager
) {
    fun getAllAccounts(): Flow<List<AccountEntity>> = accountDao.getAllAccountsFlow()

    suspend fun getSymbol(): String = settingsManager.getCurrencySymbol()
}