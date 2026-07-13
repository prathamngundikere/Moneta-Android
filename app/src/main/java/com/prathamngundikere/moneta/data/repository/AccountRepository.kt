package com.prathamngundikere.moneta.data.repository

import com.prathamngundikere.moneta.data.datastore.SettingsManager
import com.prathamngundikere.moneta.data.db.AccountDao
import com.prathamngundikere.moneta.data.db.AccountEntity
import com.prathamngundikere.moneta.data.model.dto.AccountCreateRequest
import com.prathamngundikere.moneta.data.model.dto.AccountStatementDto
import com.prathamngundikere.moneta.data.model.dto.AccountUpdateRequest
import com.prathamngundikere.moneta.data.model.enums.AccountType
import com.prathamngundikere.moneta.data.network.ApiService
import com.prathamngundikere.moneta.data.network.RetrofitFactory
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class AccountRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val settingsManager: SettingsManager,
    private val retrofitFactory: RetrofitFactory
) {
    private suspend fun getApi(): ApiService {
        val (url, key) = settingsManager.getCredentials() ?: throw Exception("Missing config")
        return retrofitFactory.create(url, key)
    }

    fun getAllAccounts(): Flow<List<AccountEntity>> = accountDao.getAllAccountsFlow()

    fun getAccountById(id: String): Flow<AccountEntity?> = accountDao.getAccountByIdFlow(id)

    suspend fun getSymbol(): String = settingsManager.getCurrencySymbol()

    suspend fun refreshAccounts(): Result<Unit> {
        return try {
            val api = getApi()
            val response = api.getAccounts()
            if (response.isSuccessful) {
                val dtos = response.body()?.content ?: emptyList()
                val entities = dtos.map {
                    AccountEntity(it.id, it.name, it.accountType, it.balance, it.isActive)
                }
                // Optional: Delete old and insert new to keep sync perfect,
                // or just rely on REPLACE strategy. We'll use REPLACE for safe upsert.
                accountDao.insertAccounts(entities)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to fetch accounts from server."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createAccount(name: String, type: AccountType, balance: Double): Result<Unit> {
        return try {
            val api = getApi()
            val request = AccountCreateRequest(name, type.name, balance)
            val response = api.createAccount(request)

            if (response.isSuccessful) {
                val dto = response.body()
                if (dto != null) {
                    val entity = AccountEntity(dto.id, dto.name, dto.accountType, dto.balance, dto.isActive)
                    accountDao.insertAccount(entity)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Account created but response was empty."))
                }
            } else {
                Result.failure(Exception("Failed to create account."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateAccountName(id: String, newName: String): Result<Unit> {
        return try {
            val currentAccount = accountDao.getAccountById(id)
                ?: return Result.failure(Exception("Account not found locally"))

            val api = getApi()
            // We only update name, keep type and isActive same as current
            val request = AccountUpdateRequest(newName, currentAccount.accountType, currentAccount.isActive)
            val response = api.updateAccount(id, request)

            if (response.isSuccessful) {
                val dto = response.body()
                if (dto != null) {
                    val updatedEntity = AccountEntity(dto.id, dto.name, dto.accountType, dto.balance, dto.isActive)
                    accountDao.updateAccount(updatedEntity)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Account updated but response was empty."))
                }
            } else {
                Result.failure(Exception("Failed to update account."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAccountHistory(accountId: String): Result<List<AccountStatementDto>> {
        return try {
            val response = getApi().getAccountTransactions(accountId)
            if (response.isSuccessful) Result.success(response.body()?.content ?: emptyList())
            else Result.failure(Exception("Failed to fetch account history"))
        } catch (e: Exception) { Result.failure(e) }
    }
}