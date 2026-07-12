package com.prathamngundikere.moneta.data.repository

import com.prathamngundikere.moneta.data.datastore.SettingsManager
import com.prathamngundikere.moneta.data.db.AccountDao
import com.prathamngundikere.moneta.data.db.AccountEntity
import com.prathamngundikere.moneta.data.model.dto.AccountInitRequest
import com.prathamngundikere.moneta.data.model.dto.CurrencyDto
import com.prathamngundikere.moneta.data.network.ApiService
import com.prathamngundikere.moneta.data.network.RetrofitFactory
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class SetupRepository @Inject constructor(
    private val retrofitFactory: RetrofitFactory,
    private val settingsManager: SettingsManager,
    private val accountDao: AccountDao
) {
    private suspend fun getApi(): ApiService {
        val (url, key) = settingsManager.getCredentials() ?: throw Exception("Missing config")
        return retrofitFactory.create(url, key)
    }

    suspend fun fetchCurrencies(): Result<List<CurrencyDto>> {
        return try {
            val response = getApi().getSupportedCurrencies()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else Result.failure(Exception("Failed to fetch currencies"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completeSetup(
        currency: CurrencyDto,
        accounts: List<AccountInitRequest>
    ): Result<Unit> {
        return try {
            val api = getApi()

            // 1. Send Currency selection
            val currencyRes = api.setCurrency(currency.code, currency.symbol)
            if (!currencyRes.isSuccessful) return Result.failure(Exception("Failed to set currency"))

            // 2. Send Accounts
            val initRes = api.initializeAccounts(accounts)
            if (!initRes.isSuccessful) return Result.failure(Exception("Failed to initialize accounts"))

            // 3. Save Accounts to Room
            val dtos = initRes.body() ?: emptyList()
            val entities = dtos.map {
                AccountEntity(it.id, it.name, it.accountType, it.balance, it.isActive)
            }
            accountDao.insertAccounts(entities)

            // 4. Verify DB insertion
            if (accountDao.getAccountCount() == 0) {
                return Result.failure(Exception("Failed to save accounts locally."))
            }

            // 5. Update DataStore status
            settingsManager.completeSetup(currency.code, currency.symbol)
            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}